package ru.develonica.load.testing.runner.service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.develonica.load.testing.common.model.HttpMethod;
import ru.develonica.load.testing.common.model.TestCaseRequest;
import ru.develonica.load.testing.common.model.generated.*;
import ru.develonica.load.testing.common.service.LoadTestingServiceGrpc;
import ru.develonica.load.testing.runner.service.load.LoadTestingService;

import java.time.Duration;

@GrpcService
public class LoadTestingStartService extends LoadTestingServiceGrpc.LoadTestingServiceImplBase {

    LoadTestingService loadTestingService;

    public LoadTestingStartService(LoadTestingService loadTestingService) {
        this.loadTestingService = loadTestingService;
    }

    @Override
    public void start(LoadTestingStartRequest request, StreamObserver<LoadTestingStartResponse> responseObserver) {
        TestCaseRequest testCaseRequest = new TestCaseRequest();
        testCaseRequest.setUrl(request.getUrl());
        testCaseRequest.setBody(request.getBody());
        testCaseRequest.setHeader(request.getHeaderMap());
        testCaseRequest.setMethod(HttpMethod.valueOf(request.getMethod().name()));

        String jmxHost = request.getJmxHost();
        Integer jmxPort = request.getJmxPort();
        String status =  loadTestingService.start(Duration.parse(request.getDuration()), request.getParallelRequests(), testCaseRequest, jmxHost, jmxPort);

        LoadTestingStartResponse response = LoadTestingStartResponse.newBuilder().setStatus(Status.valueOf(status)).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void metrics(Empty request, StreamObserver<Metrics> responseObserver) {
        Metrics metrics = loadTestingService.getMetrics();

        responseObserver.onNext(metrics);
        responseObserver.onCompleted();
    }
}
