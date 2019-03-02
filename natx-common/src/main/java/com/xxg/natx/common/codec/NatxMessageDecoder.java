package com.xxg.natx.common.codec;

import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by wucao on 2019/3/2.
 */
public class NatxMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {

        int type = msg.readInt();
        NatxMessageType natxMessageType = NatxMessageType.valueOf(type);

        int metaDataLength = msg.readInt();
        CharSequence metaDataString = msg.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String, Object> metaData = jsonObject.toMap();

        byte[] data = ByteBufUtil.getBytes(msg);

        NatxMessage natxMessage = new NatxMessage();
        natxMessage.setType(natxMessageType);
        natxMessage.setMetaData(metaData);
        natxMessage.setData(data);

        out.add(natxMessage);
    }

}
