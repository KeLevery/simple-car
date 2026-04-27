package com.simplecar.controller;

import com.simplecar.service.ChargingService;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChargingControllerTest {

    @Test
    void passesAvailableOnlyFilterToService() {
        ChargingService chargingService = mock(ChargingService.class);
        ChargingController controller = new ChargingController(chargingService);
        when(chargingService.listByCity("320100", true)).thenReturn(Collections.emptyList());

        controller.listStations("320100", true);

        verify(chargingService).listByCity("320100", true);
    }
}
