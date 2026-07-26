import {
  closeToast,
  showConfirmDialog,
  showDialog,
  showFailToast,
  showLoadingToast,
  showNotify,
  showSuccessToast,
  showToast
} from 'vant'

const toast = Object.assign(showToast, {
  success: showSuccessToast,
  fail: showFailToast,
  loading: showLoadingToast,
  clear: closeToast
})

const dialog = {
  confirm: showConfirmDialog,
  alert: showDialog
}

export function useVantCompat() {
  return {
    toast,
    notify: showNotify,
    dialog
  }
}
