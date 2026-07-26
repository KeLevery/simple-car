package com.simplecar.service;

import com.simplecar.model.entity.ChargingStation;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.model.entity.ServiceStation;
import com.simplecar.result.PagedData;

import java.util.Map;

public interface AdminService {
    Map<String, Object> overview();

    PagedData<Map<String, Object>> listUsers(String keyword, Integer pageNum, Integer pageSize);

    Map<String, Object> createUser(Map<String, Object> params);

    Map<String, Object> updateUser(Long id, Map<String, Object> params);

    boolean updateUserStatus(Long id, Integer status);

    boolean deleteUser(Long id);

    PagedData<Map<String, Object>> listVehicles(Long userId, Integer pageNum, Integer pageSize);

    Map<String, Object> createVehicle(Map<String, Object> params);

    Map<String, Object> updateVehicle(Long id, Map<String, Object> params);

    boolean deleteVehicle(Long id);

    PagedData<MaintenanceAppointment> listAppointments(String keyword, Integer pageNum, Integer pageSize);

    boolean updateAppointmentStatus(Long id, Integer status);

    PagedData<RescueRequest> listRescues(String keyword, Integer pageNum, Integer pageSize);

    boolean updateRescueStatus(Long id, Integer status);

    PagedData<ChargingStation> listChargingStations(String keyword, Integer pageNum, Integer pageSize);

    ChargingStation createChargingStation(Map<String, Object> params);

    ChargingStation updateChargingStation(Long id, Map<String, Object> params);

    boolean updateChargingStationStatus(Long id, Integer status);

    boolean deleteChargingStation(Long id);

    PagedData<ServiceStation> listServiceStations(String keyword, Integer pageNum, Integer pageSize);

    ServiceStation createServiceStation(Map<String, Object> params);

    ServiceStation updateServiceStation(Long id, Map<String, Object> params);

    boolean deleteServiceStation(Long id);

    PagedData<CommunityPost> listCommunityPosts(String keyword, Integer pageNum, Integer pageSize);

    boolean deleteCommunityPost(Long id);
}
