package com.simplecar.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplecar.model.dto.AppointmentRequest;
import com.simplecar.model.entity.MaintenanceAppointment;
import com.simplecar.model.entity.MaintenancePlan;
import com.simplecar.model.entity.ServiceStation;

import java.util.List;
import java.util.Map;

public interface MaintenanceService {
    List<MaintenancePlan> getPlans();

    Map<String, Object> createAppointment(AppointmentRequest request);

    Page<MaintenanceAppointment> getAppointmentPage(Long carId, Integer pageNum, Integer pageSize);

    Page<ServiceStation> getStationPage(String cityId, Integer pageNum, Integer pageSize);

    boolean updatePayStatus(Long payId, Integer status, Long appointmentId);
}
