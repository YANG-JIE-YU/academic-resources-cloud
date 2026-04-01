from __future__ import annotations

import argparse
import csv
import json
import math
import statistics
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Iterable
from urllib.parse import urlencode
from urllib.request import Request, urlopen


DEFAULT_URL = "http://127.0.0.1:18080/api/paper/papers/page"
DEFAULT_LEVELS = "10,20,50,100"
DEFAULT_KEYWORD = "学术"


@dataclass
class RequestResult:
    ok: bool
    elapsed_ms: float
    status_code: int
    error_message: str = ""


@dataclass
class LevelResult:
    concurrency: int
    total_requests: int
    success_count: int
    error_count: int
    total_duration_s: float
    avg_ms: float
    p95_ms: float
    throughput: float


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Chapter 7 performance test for /api/paper/papers/page using urllib + ThreadPoolExecutor."
    )
    parser.add_argument(
        "--url",
        default=DEFAULT_URL,
        help="Target endpoint URL. Example: http://127.0.0.1:18080/api/paper/papers/page",
    )
    parser.add_argument("--current", type=int, default=1, help="Query parameter current")
    parser.add_argument("--size", type=int, default=10, help="Query parameter size")
    parser.add_argument("--keyword", default=DEFAULT_KEYWORD, help="Query parameter keyword")
    parser.add_argument("--category-id", type=int, default=None, help="Optional categoryId")
    parser.add_argument(
        "--levels",
        default=DEFAULT_LEVELS,
        help="Comma-separated concurrency levels, e.g. 10,20,50,100",
    )
    parser.add_argument(
        "--rounds",
        type=int,
        default=5,
        help="Requests per worker under each concurrency level",
    )
    parser.add_argument("--timeout", type=float, default=10.0, help="Single request timeout in seconds")
    parser.add_argument("--warmup", type=int, default=3, help="Warmup request count before the formal test")
    parser.add_argument(
        "--output",
        default="",
        help="Optional CSV output path. If omitted, a timestamped file is created under docs/_thesis_work/results",
    )
    return parser.parse_args()


def build_url(base_url: str, current: int, size: int, keyword: str, category_id: int | None) -> str:
    query = {
        "current": current,
        "size": size,
    }
    if keyword:
        query["keyword"] = keyword
    if category_id is not None:
        query["categoryId"] = category_id
    return f"{base_url}?{urlencode(query)}"


def percentile(sorted_values: list[float], ratio: float) -> float:
    if not sorted_values:
        return 0.0
    if len(sorted_values) == 1:
        return sorted_values[0]
    rank = math.ceil(len(sorted_values) * ratio) - 1
    rank = max(0, min(rank, len(sorted_values) - 1))
    return sorted_values[rank]


def do_request(url: str, timeout: float) -> RequestResult:
    start = time.perf_counter()
    request = Request(
        url,
        method="GET",
        headers={
            "User-Agent": "academic-cloud-thesis-ch7-test/1.0",
            "Accept": "application/json",
        },
    )
    try:
        with urlopen(request, timeout=timeout) as response:
            body = response.read()
            elapsed_ms = (time.perf_counter() - start) * 1000
            status_code = getattr(response, "status", 200)
            payload = json.loads(body.decode("utf-8"))
            ok = status_code == 200 and payload.get("code") == 0
            error_message = ""
            if not ok:
                error_message = f"unexpected response: status={status_code}, code={payload.get('code')}, msg={payload.get('msg')}"
            return RequestResult(ok=ok, elapsed_ms=elapsed_ms, status_code=status_code, error_message=error_message)
    except Exception as exc:  # noqa: BLE001
        elapsed_ms = (time.perf_counter() - start) * 1000
        return RequestResult(ok=False, elapsed_ms=elapsed_ms, status_code=0, error_message=str(exc))


def warmup(url: str, timeout: float, warmup_count: int) -> None:
    for _ in range(max(0, warmup_count)):
        do_request(url, timeout)


def run_level(url: str, concurrency: int, rounds: int, timeout: float) -> tuple[LevelResult, list[RequestResult]]:
    total_requests = concurrency * rounds
    request_results: list[RequestResult] = []
    level_start = time.perf_counter()

    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        futures = [executor.submit(do_request, url, timeout) for _ in range(total_requests)]
        for future in as_completed(futures):
            request_results.append(future.result())

    total_duration_s = time.perf_counter() - level_start
    latencies = sorted(item.elapsed_ms for item in request_results if item.ok)
    success_count = sum(1 for item in request_results if item.ok)
    error_count = total_requests - success_count
    avg_ms = statistics.fmean(latencies) if latencies else 0.0
    p95_ms = percentile(latencies, 0.95)
    throughput = (total_requests / total_duration_s) if total_duration_s > 0 else 0.0

    return (
        LevelResult(
            concurrency=concurrency,
            total_requests=total_requests,
            success_count=success_count,
            error_count=error_count,
            total_duration_s=total_duration_s,
            avg_ms=avg_ms,
            p95_ms=p95_ms,
            throughput=throughput,
        ),
        request_results,
    )


def parse_levels(level_text: str) -> list[int]:
    levels: list[int] = []
    for chunk in level_text.split(","):
        value = chunk.strip()
        if not value:
            continue
        parsed = int(value)
        if parsed <= 0:
            raise ValueError(f"Invalid concurrency level: {parsed}")
        levels.append(parsed)
    if not levels:
        raise ValueError("No concurrency level provided")
    return levels


def ensure_output_path(output: str) -> Path:
    if output:
        return Path(output).resolve()
    root = Path(__file__).resolve().parent
    out_dir = root / "results"
    out_dir.mkdir(parents=True, exist_ok=True)
    stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    return out_dir / f"ch7_perf_result_{stamp}.csv"


def save_csv(output_path: Path, rows: Iterable[LevelResult]) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", newline="", encoding="utf-8-sig") as file:
        writer = csv.writer(file)
        writer.writerow(
            [
                "concurrency",
                "total_requests",
                "success_count",
                "error_count",
                "success_rate",
                "avg_ms",
                "p95_ms",
                "throughput_req_per_s",
                "total_duration_s",
            ]
        )
        for row in rows:
            success_rate = (row.success_count / row.total_requests) if row.total_requests else 0.0
            writer.writerow(
                [
                    row.concurrency,
                    row.total_requests,
                    row.success_count,
                    row.error_count,
                    f"{success_rate:.2%}",
                    f"{row.avg_ms:.2f}",
                    f"{row.p95_ms:.2f}",
                    f"{row.throughput:.2f}",
                    f"{row.total_duration_s:.2f}",
                ]
            )


def print_summary(url: str, rows: list[LevelResult], output_path: Path) -> None:
    print("=" * 84)
    print(f"Target URL : {url}")
    print(f"CSV Output : {output_path}")
    print("=" * 84)
    print(
        f"{'Concurrency':>12} {'Requests':>10} {'Success':>10} {'Errors':>8} "
        f"{'Avg(ms)':>10} {'P95(ms)':>10} {'TPS':>10}"
    )
    for row in rows:
        print(
            f"{row.concurrency:>12} {row.total_requests:>10} {row.success_count:>10} {row.error_count:>8} "
            f"{row.avg_ms:>10.2f} {row.p95_ms:>10.2f} {row.throughput:>10.2f}"
        )
    print("=" * 84)


def main() -> int:
    args = parse_args()
    levels = parse_levels(args.levels)
    target_url = build_url(args.url, args.current, args.size, args.keyword, args.category_id)
    output_path = ensure_output_path(args.output)

    print(f"Warmup requests: {args.warmup}")
    warmup(target_url, args.timeout, args.warmup)

    rows: list[LevelResult] = []
    all_errors: list[str] = []

    for level in levels:
        print(f"Running concurrency level {level} ...")
        row, request_results = run_level(target_url, level, args.rounds, args.timeout)
        rows.append(row)
        for item in request_results:
            if not item.ok and item.error_message:
                all_errors.append(item.error_message)

    save_csv(output_path, rows)
    print_summary(target_url, rows, output_path)

    if all_errors:
        print("Sample errors:")
        for message in all_errors[:5]:
            print(f"- {message}")
        return 1

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
