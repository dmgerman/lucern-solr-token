begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package
begin_comment
comment|/**  * SmartChineseAnalyzer internal token  */
end_comment
begin_class
DECL|class|SegToken
specifier|public
class|class
name|SegToken
block|{
DECL|field|charArray
specifier|public
name|char
index|[]
name|charArray
decl_stmt|;
DECL|field|startOffset
specifier|public
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|public
name|int
name|endOffset
decl_stmt|;
DECL|field|wordType
specifier|public
name|int
name|wordType
decl_stmt|;
DECL|field|weight
specifier|public
name|int
name|weight
decl_stmt|;
DECL|field|index
specifier|public
name|int
name|index
decl_stmt|;
DECL|method|SegToken
specifier|public
name|SegToken
parameter_list|(
name|String
name|word
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|wordType
parameter_list|,
name|int
name|weight
parameter_list|)
block|{
name|this
operator|.
name|charArray
operator|=
name|word
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|wordType
operator|=
name|wordType
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
DECL|method|SegToken
specifier|public
name|SegToken
parameter_list|(
name|char
index|[]
name|idArray
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|int
name|wordType
parameter_list|,
name|int
name|weight
parameter_list|)
block|{
name|this
operator|.
name|charArray
operator|=
name|idArray
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|end
expr_stmt|;
name|this
operator|.
name|wordType
operator|=
name|wordType
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|// public String toString() {
comment|// return String.valueOf(charArray) + "/s(" + startOffset + ")e("
comment|// + endOffset + ")/w(" + weight + ")t(" + wordType + ")";
comment|// }
comment|// public boolean equals(RawToken t) {
comment|// return this.startOffset == t.startOffset
comment|//&& this.endOffset == t.endOffset;
comment|// }
block|}
end_class
end_unit
