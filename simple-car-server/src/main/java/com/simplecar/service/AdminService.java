package com.simplecar.service;

import com.simplecar.model.entity.ChargingStation;
import com.simplecar.model.entity.CommunityPost;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.RescueRequest;
import com.simplecar.model.entity.ServiceStation;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Object> overview();

    List<Map<String, Object>> listUsers(String keyword, Integer limit);

    Map<String, Object> createUser(Map<String, Object> params);

    Map<String, Object> updateUser(Long id, Map<String, Object> params);

    boolean updateUserStatus(Long id, Integer status);

    boolean deleteUser(Long id);

    List<Map<String, Object>> listVehicles(Long userId);

    Map<String, Object> createVehicle(Map<String, Object> params);

    Map<String, Object> updateVehicle(Long id, Map<String, Object> params);

    boolean deleteVehicle(Long id);

    List<MaintenanceAppointment> listAppointments();

    boolean updateAppointmentStatus(Long id, Integer status);

    List<RescueRequest> listRescues();

    boolean updateRescueStatus(Long id, Integer status);

    List<ChargingStation> listChargingStations();

    ChargingStation createChargingStation(Map<String, Object> params);

    ChargingStation updateChargingStation(Long id, Map<String, Object> params);

    boolean updateChargingStationStatus(Long id, Integer status);

    boolean deleteChargingStation(Long id);

    List<ServiceStation> listServiceStations();

    ServiceStation createServiceStation(Map<String, Object> params);

    ServiceStation updateServiceStation(Long id, Map<String, Object> params);

    boolean deleteServiceStation(Long id);

    List<CommunityPost> listCommunityPosts();

    boolean deleteCommunityPost(Long id);
}
