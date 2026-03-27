import { reactive } from 'vue'

export function usePagination(defaultSize = 10) {
  const pager = reactive({
    current: 1,
    size: defaultSize,
    total: 0
  })

  function reset() {
    pager.current = 1
    pager.total = 0
  }

  function update(total: number) {
    pager.total = total
  }

  return {
    pager,
    reset,
    update
  }
}
