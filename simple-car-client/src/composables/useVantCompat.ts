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

type ToastCompat = typeof showToast & {
  success: typeof showSuccessToast
  fail: typeof showFailToast
  loading: typeof showLoadingToast
  clear: typeof closeToast
}

interface DialogCompat {
  confirm: typeof showConfirmDialog
  alert: typeof showDialog
}

const toast: ToastCompat = Object.assign(showToast, {
  success: showSuccessToast,
  fail: showFailToast,
  loading: showLoadingToast,
  clear: closeToast
})

const dialog: DialogCompat = {
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
