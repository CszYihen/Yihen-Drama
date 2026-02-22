package org.springframework.core.codec;
 
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
 
 
/**
 * @Auther: whhh
 * @Date: 2021/4/7 11:08
 * @Description:  图片上传重写缓存区大小，解决上传图片缓存不够问题
 */
public abstract class AbstractDataBufferDecoder<T> extends AbstractDecoder<T> {
    //图片上传重写缓存区大小，解决上传图片缓存不够问题
    private int maxInMemorySize = 1024*1024*100;
 
    protected AbstractDataBufferDecoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
    }
 
    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }
 
    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }
 
    public Flux<T> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(input).map((buffer) -> {
            return this.decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }
 
    public Mono<T> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(input, this.maxInMemorySize).map((buffer) -> {
            return this.decodeDataBuffer(buffer, elementType, mimeType, hints);
        });
    }
 
    /** @deprecated */
    @Deprecated
    @Nullable
    protected T decodeDataBuffer(DataBuffer buffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return this.decode(buffer, elementType, mimeType, hints);
    }
}