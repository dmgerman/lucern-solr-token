begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_comment
comment|/**  * CharReader is a Reader wrapper. It reads chars from  * Reader and outputs {@link CharStream}, defining an  * identify function {@link #correctOffset} method that  * simply returns the provided offset.  */
end_comment
begin_class
DECL|class|CharReader
specifier|public
specifier|final
class|class
name|CharReader
extends|extends
name|CharStream
block|{
DECL|field|input
specifier|protected
name|Reader
name|input
decl_stmt|;
DECL|method|get
specifier|public
specifier|static
name|CharStream
name|get
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
name|input
operator|instanceof
name|CharStream
condition|?
operator|(
name|CharStream
operator|)
name|input
else|:
operator|new
name|CharReader
argument_list|(
name|input
argument_list|)
return|;
block|}
DECL|method|CharReader
specifier|private
name|CharReader
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|input
operator|=
name|in
expr_stmt|;
block|}
DECL|method|correctOffset
specifier|public
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|currentOff
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|input
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
name|input
operator|.
name|markSupported
argument_list|()
return|;
block|}
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readAheadLimit
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|mark
argument_list|(
name|readAheadLimit
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
