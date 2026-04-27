import request from "@/util/request"

//查询充电订单列表
export function chargeOrderList(data){
	return request({
		url:'/bs-vehicle-owner/charging-orders/list',
		method:'post',
		data:data
	})
}

//查询总行驶里程
export function mileageList(data){
	return request({
		url:'/bs-vehicle-owner/vehicle-mileage/list',
		method:'post',
		data:data
	})
}