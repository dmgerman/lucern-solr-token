begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A codec that forwards all its method calls to another codec.  *<p>  * Extend this class when you need to reuse the functionality of an existing  * codec. For example, if you want to build a codec that redefines Lucene41's  * {@link LiveDocsFormat}:  *<pre class="prettyprint">  *   public final class CustomCodec extends FilterCodec {  *  *     public CustomCodec() {  *       super("CustomCodec", new Lucene41Codec());  *     }  *  *     public LiveDocsFormat liveDocsFormat() {  *       return new CustomLiveDocsFormat();  *     }  *  *   }  *</pre>  *   *<p><em>Please note:</em> Don't call {@link Codec#forName} from  * the no-arg constructor of your own codec. When the SPI framework  * loads your own Codec as SPI component, SPI has not yet fully initialized!  * If you want to extend another Codec, instantiate it directly by calling  * its constructor.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FilterCodec
specifier|public
specifier|abstract
class|class
name|FilterCodec
extends|extends
name|Codec
block|{
comment|/** The codec to filter. */
DECL|field|delegate
specifier|protected
specifier|final
name|Codec
name|delegate
decl_stmt|;
comment|/** Sole constructor. When subclassing this codec,    * create a no-arg ctor and pass the delegate codec    * and a unique name to this ctor.    */
DECL|method|FilterCodec
specifier|protected
name|FilterCodec
parameter_list|(
name|String
name|name
parameter_list|,
name|Codec
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|docValuesFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|simpleDocValuesFormat
specifier|public
name|SimpleDocValuesFormat
name|simpleDocValuesFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|simpleDocValuesFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|fieldInfosFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|liveDocsFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|NormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|normsFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|simpleNormsFormat
specifier|public
name|SimpleNormsFormat
name|simpleNormsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|simpleNormsFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|postingsFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|segmentInfoFormat
specifier|public
name|SegmentInfoFormat
name|segmentInfoFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|segmentInfoFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|storedFieldsFormat
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|termVectorsFormat
argument_list|()
return|;
block|}
block|}
end_class
end_unit
