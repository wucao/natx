package com.xxg.natx.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by wucao on 2019/2/28.
 */
public class NatxRegisterDecoder extends ByteToMessageDecoder {

    private boolean register = false;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (!register) {
            if (in.readableBytes() >= 4) {
                in.markReaderIndex();
                int bodyLength = in.readInt();
                if (in.readableBytes() < bodyLength) {
                    in.resetReaderIndex();
                } else {
                    byte[] bodyBytes = new byte[bodyLength];
                    in.readBytes(bodyBytes);
                    String body = new String(bodyBytes, "UTF-8");
                    JSONObject data = new JSONObject(body);
                    RegisterInfo registerInfo = new RegisterInfo();
                    registerInfo.setPort(data.getInt("port"));
                    registerInfo.setToken(data.optString("token"));
                    out.add(registerInfo);
                    register = true;
                }
            }
        } else {
            byte[] data = new byte[in.readableBytes()];
            in.readBytes(data);
            out.add(data);
        }
    }
}
