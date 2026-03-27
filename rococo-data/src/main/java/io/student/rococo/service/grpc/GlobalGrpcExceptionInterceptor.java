package io.student.rococo.service.grpc;

import io.grpc.*;
import io.student.rococo.exception.*;
import jakarta.persistence.EntityExistsException;

public class GlobalGrpcExceptionInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);

        return new ForwardingServerCallListener
                .SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (FieldValidationException | IllegalArgumentException e) {
                    call.close(Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage()), new Metadata());
                } catch (EntityExistsException e) {
                    call.close(Status.ALREADY_EXISTS
                            .withDescription(e.getMessage()), new Metadata());
                } catch (ArtistNotFoundException | MuseumNotFoundException | CountryNotFoundException | PaintingNotFoundException e) {
                    call.close(Status.NOT_FOUND
                            .withDescription(e.getMessage()), new Metadata());
                }  catch (RuntimeException e) {
                    call.close(Status.INTERNAL
                            .withDescription(e.getMessage()), new Metadata());
                } catch (Exception e) {
                    call.close(Status.INTERNAL
                                    .withDescription("Internal server error"),
                            new Metadata());
                }
            }
        };
    }
}