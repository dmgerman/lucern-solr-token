begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermState
import|;
end_import
begin_class
DECL|class|TempBlockTermState
specifier|public
class|class
name|TempBlockTermState
extends|extends
name|TempTermState
block|{
comment|/** the term's ord in the current block */
DECL|field|termBlockOrd
specifier|public
name|int
name|termBlockOrd
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|TempBlockTermState
specifier|protected
name|TempBlockTermState
parameter_list|()
block|{   }
DECL|method|clone
specifier|public
name|TempBlockTermState
name|clone
parameter_list|()
block|{
name|TempBlockTermState
name|other
init|=
operator|(
name|TempBlockTermState
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
return|return
name|other
return|;
block|}
annotation|@
name|Override
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|TermState
name|_other
parameter_list|)
block|{
assert|assert
name|_other
operator|instanceof
name|TempBlockTermState
operator|:
literal|"can not copy from "
operator|+
name|_other
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
assert|;
name|super
operator|.
name|copyFrom
argument_list|(
name|_other
argument_list|)
expr_stmt|;
name|TempBlockTermState
name|other
init|=
operator|(
name|TempBlockTermState
operator|)
name|_other
decl_stmt|;
name|termBlockOrd
operator|=
name|other
operator|.
name|termBlockOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" termBlockOrd="
operator|+
name|termBlockOrd
return|;
block|}
block|}
end_class
end_unit
