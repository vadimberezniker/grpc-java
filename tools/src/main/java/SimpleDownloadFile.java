package com.google.devtools.build.tools;

import com.google.bytestream.ByteStreamGrpc;
import com.google.bytestream.ByteStreamProto;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.logging.Logger;

public class SimpleDownloadFile {
    private static final Logger logger = Logger.getLogger(SimpleDownloadFile.class.getName());

    public static void main(String[] args) throws Exception{
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tl:%1$tM:%1$tS.%1$tL %4$s: %5$s%6$s%n");

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
        logger.info("Finished downloading file in " + elapsed);
    }
}
