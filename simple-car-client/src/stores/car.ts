import { defineStore } from 'pinia'
import { getJSON, setJSON, remove } from '@/util/storage'

export interface CarItem {
  id?: number
  carId?: number
  carName?: string
  carModels?: string
  licenseTag?: string
  [key: string]: unknown
}

/**
 * 车辆态 store。持久化沿用旧 localStorage key（carInfo/carList，JSON 格式），
 * 与未迁移页面双写共存。
 */
export const useCarStore = defineStore('car', {
  state: () => ({
    carList: getJSON<CarItem[]>('carList') || [],
    carInfo: getJSON<CarItem>('carInfo')
  }),
  actions: {
    setCars(list: CarItem[]) {
      this.carList = list
      setJSON('carList', list)
      if (list.length > 0) {
        this.carInfo = list[0]
        setJSON('carInfo', list[0])
      }
    },
    selectCar(car: CarItem) {
      this.carInfo = car
      setJSON('carInfo', car)
    },
    clear() {
      this.carList = []
      this.carInfo = null
      remove('carInfo')
      remove('carList')
    }
  }
})
