package io.student.rococo.service.grpc;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.student.rococo.exception.GrpcStatusException;
import io.student.rococo.grpc.*;
import io.student.rococo.model.MuseumJson;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static io.student.rococo.utils.GrpcUtils.springPageableToGrpcPageableRequest;

@Component
public class GrpcMuseumClient {

    @GrpcClient("grpcDataClient")
    private MuseumServiceGrpc.MuseumServiceBlockingStub stub;

    public Page<MuseumJson> getAllMuseums(Pageable pageable) {
        try {
            PageableRequest req = springPageableToGrpcPageableRequest(pageable);
            MuseumsResponse resp = stub.allMuseums(req);

            List<MuseumJson> items = resp.getMuseumsList().stream()
                    .map(MuseumJson::fromGrpcMessage)
                    .toList();

            return new PageImpl<>(items, pageable, resp.getTotalElements());
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public MuseumJson getMuseumById(UUID id) {
        try {
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Museum id is required");
            }

            IdRequest req = IdRequest.newBuilder()
                    .setId(id.toString())
                    .build();

            MuseumResponse resp = stub.findMuseumById(req);
            return MuseumJson.fromGrpcMessage(resp);
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public MuseumJson createMuseum(MuseumJson museumJson) {
        try {
            CreateMuseumRequest.Builder builder = CreateMuseumRequest.newBuilder()
                    .setTitle(museumJson.title() == null ? "" : museumJson.title())
                    .setDescription(museumJson.description() == null ? "" : museumJson.description());

            Geo geo = Geo.newBuilder()
                    .setCity(museumJson.geo().city() == null ? "" : museumJson.geo().city() )
                    .setCountryId(museumJson.geo().country().id() == null ? "" : museumJson.geo().country().id().toString())
                    .build();
            builder.setGeo(geo);

            if (museumJson.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(museumJson.photo())));
            }

            MuseumResponse resp = stub.createMuseum(builder.build());
            return MuseumJson.fromGrpcMessage(resp);
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }

    public MuseumJson updateMuseum(MuseumJson museumJson) {
        try {
            if (museumJson.id() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Museum id is required");
            }

            UpdateMuseumRequest.Builder builder = UpdateMuseumRequest.newBuilder()
                    .setId(museumJson.id().toString());

            if (museumJson.title() != null) {
                builder.setTitle(museumJson.title());
            }
            if (museumJson.description() != null) {
                builder.setDescription(museumJson.description());
            }

            if (museumJson.geo().city() != null || museumJson.geo().country().id() != null) {
                Geo.Builder geoBuilder = Geo.newBuilder();
                geoBuilder.setCity(museumJson.geo().city() == null ? "" : museumJson.geo().city());
                geoBuilder.setCountryId(museumJson.geo().country().id()  == null ? "" : museumJson.geo().country().id().toString());
                builder.setGeo(geoBuilder.build());
            }

            if (museumJson.photo() != null) {
                builder.setPhoto(ByteString.copyFrom(decodeImageFromB64ToBytes(museumJson.photo())));
            }

            MuseumResponse resp = stub.updateMuseum(builder.build());
            return MuseumJson.fromGrpcMessage(resp);
        } catch (StatusRuntimeException e) {
            throw new GrpcStatusException(e);
        }
    }
}