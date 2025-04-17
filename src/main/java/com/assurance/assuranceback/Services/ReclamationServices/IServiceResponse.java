package com.assurance.assuranceback.Services.ReclamationServices;

import com.assurance.assuranceback.Entity.ReclamationEntity.Response;

import java.util.List;
import java.util.Optional;

public interface IServiceResponse {
    Response createResponse(Response response);

    List<Response> getAllResponses();

    Optional<Response> getResponseById(Long id);

    Response updateResponse(Long id, Response responseDetails);

    void deleteResponse(Long id_response);
}
