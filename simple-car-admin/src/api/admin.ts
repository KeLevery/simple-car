import { http, request, requestPage } from './http'

export interface PageQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
}

export interface Overview {
  userCount: number
  vehicleCount: number
  appointmentCount: number
  rescueCount: number
  chargingStationCount: number
  serviceStationCount: number
  postCount: number
  chargingRevenue: number
  maintenanceRevenue: number
  todayAppointmentCount: number
  pendingRescueCount: number
}

export interface UserItem {
  id: number
  username: string
  nickName: string
  phone: string
  status: number
  createdAt: string
}

export interface UserPayload {
  username?: string
  password?: string
  nickName?: string
  phone?: string
  status: number
}

export interface VehicleItem {
  id: number
  userId: number
  userName: string
  username: string
  carName: string
  carModels: string
  licenseTag: string
  frameNumber: string
  enduranceMileage: number
  remainingPower: number
  temperature: number
  carState: number
  updatedAt: string
}

export interface VehiclePayload {
  userId: number
  carName: string
  carModels: string
  licenseTag: string
  frameNumber: string
  enduranceMileage: number
  remainingPower: number
  temperature: number
  carState: number
}

export interface AppointmentItem {
  id: number
  workNo: string
  customerName: string
  customerPhone: string
  appointDate: string
  appointTime: string
  totalAmount: number
  status: number
  createdAt: string
}

export interface RescueItem {
  id: number
  contactName: string
  contactPhone: string
  location: string
  description: string
  status: number
  createTime: string
}

export interface ChargingStationItem {
  id: number
  stationName: string
  address: string
  cityId: string
  totalPiles: number
  availablePiles: number
  status: number
}

export type ChargingStationPayload = Omit<ChargingStationItem, 'id'>

export interface ServiceStationItem {
  id: number
  serviceStationName: string
  cityId: string
  address: string
  phone: string
  status: number
}

export type ServiceStationPayload = Omit<ServiceStationItem, 'id'>

export interface CommunityPostItem {
  id: number
  userId: number
  content: string
  likeCount: number
  commentCount: number
  shareCount: number
  isHot: number
  createTime: string
}

export interface AdminSession {
  username: string
  roles: string[]
  authenticated: boolean
}

export interface AdminLoginResult {
  token: string
  username: string
  roles: string[]
}

export async function login(username: string, password: string) {
  return request<AdminLoginResult>('/admin/auth/login', {
    method: 'post',
    data: { username, password }
  })
}

export const adminApi = {
  session: () => request<AdminSession>('/admin/session'),
  overview: () => request<Overview>('/admin/overview'),
  users: (query: PageQuery = {}) => requestPage<UserItem>('/admin/users', { params: query }),
  createUser: (payload: UserPayload) => http.post('/admin/users', payload),
  updateUser: (id: number, payload: UserPayload) => http.put(`/admin/users/${id}`, payload),
  deleteUser: (id: number) => http.delete(`/admin/users/${id}`),
  vehicles: (query: PageQuery & { userId?: number } = {}) =>
    requestPage<VehicleItem>('/admin/vehicles', { params: query }),
  createVehicle: (payload: VehiclePayload) => http.post('/admin/vehicles', payload),
  updateVehicle: (id: number, payload: VehiclePayload) => http.put(`/admin/vehicles/${id}`, payload),
  deleteVehicle: (id: number) => http.delete(`/admin/vehicles/${id}`),
  appointments: (query: PageQuery = {}) => requestPage<AppointmentItem>('/admin/appointments', { params: query }),
  rescues: (query: PageQuery = {}) => requestPage<RescueItem>('/admin/rescues', { params: query }),
  chargingStations: (query: PageQuery = {}) =>
    requestPage<ChargingStationItem>('/admin/charging-stations', { params: query }),
  createChargingStation: (payload: ChargingStationPayload) => http.post('/admin/charging-stations', payload),
  updateChargingStation: (id: number, payload: ChargingStationPayload) => http.put(`/admin/charging-stations/${id}`, payload),
  deleteChargingStation: (id: number) => http.delete(`/admin/charging-stations/${id}`),
  serviceStations: (query: PageQuery = {}) =>
    requestPage<ServiceStationItem>('/admin/service-stations', { params: query }),
  createServiceStation: (payload: ServiceStationPayload) => http.post('/admin/service-stations', payload),
  updateServiceStation: (id: number, payload: ServiceStationPayload) => http.put(`/admin/service-stations/${id}`, payload),
  deleteServiceStation: (id: number) => http.delete(`/admin/service-stations/${id}`),
  communityPosts: (query: PageQuery = {}) =>
    requestPage<CommunityPostItem>('/admin/community-posts', { params: query }),
  updateUserStatus: (id: number, status: number) => http.put(`/admin/users/${id}/status`, { status }),
  updateAppointmentStatus: (id: number, status: number) => http.put(`/admin/appointments/${id}/status`, { status }),
  updateRescueStatus: (id: number, status: number) => http.put(`/admin/rescues/${id}/status`, { status }),
  updateChargingStationStatus: (id: number, status: number) => http.put(`/admin/charging-stations/${id}/status`, { status }),
  deleteCommunityPost: (id: number) => http.delete(`/admin/community-posts/${id}`)
}
