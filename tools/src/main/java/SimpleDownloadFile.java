package com.google.devtools.build.tools;

import com.google.bytestream.ByteStreamGrpc;
import com.google.bytestream.ByteStreamProto;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;

public class SimpleDownloadFile {
    public static void main(String[] args) throws Exception{
        if (args.length < 2) {
            throw new IllegalArgumentException("specify target & digest+size");
        }

        String target = args[0];
        String hashAndSize = args[1];

        ManagedChannel channel =
                NettyChannelBuilder.forTarget(target)
                .defaultLoadBalancingPolicy("round_robin")
                        .negotiationType(NegotiationType.PLAINTEXT)
                .build();

        ByteStreamGrpc.ByteStreamBlockingStub stub = ByteStreamGrpc.newBlockingStub(channel);

        LocalDateTime start = LocalDateTime.now();
        Iterator<ByteStreamProto.ReadResponse> responses = stub.read(ByteStreamProto.ReadRequest.newBuilder()
                .setResourceName("blobs/" + hashAndSize)
                .build());
        while (responses.hasNext()) {
            responses.next();
        }
        Duration elapsed = Duration.between(start, LocalDateTime.now());
        System.out.println("Finished downloading file in " + elapsed);
    }
}
