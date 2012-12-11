begin_unit
begin_package
DECL|package|org.apache.lucene.facet.enhancements
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|CategoryEnhancement
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|params
operator|.
name|EnhancementsIndexingParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryProperty
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
operator|.
name|CategoryListTokenizer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyWriter
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|CategoryEnhancementDummy3
specifier|public
class|class
name|CategoryEnhancementDummy3
implements|implements
name|CategoryEnhancement
block|{
DECL|field|CATEGORY_TOKEN_BYTES
specifier|public
specifier|static
name|byte
index|[]
name|CATEGORY_TOKEN_BYTES
init|=
operator|new
name|byte
index|[]
block|{
literal|5
block|,
operator|-
literal|1
block|,
literal|33
block|,
literal|8
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|generatesCategoryList
specifier|public
name|boolean
name|generatesCategoryList
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getCategoryListTermText
specifier|public
name|String
name|getCategoryListTermText
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCategoryListTokenizer
specifier|public
name|CategoryListTokenizer
name|getCategoryListTokenizer
parameter_list|(
name|TokenStream
name|tokenizer
parameter_list|,
name|EnhancementsIndexingParams
name|indexingParams
parameter_list|,
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getCategoryTokenBytes
specifier|public
name|byte
index|[]
name|getCategoryTokenBytes
parameter_list|(
name|CategoryAttribute
name|categoryAttribute
parameter_list|)
block|{
return|return
name|CATEGORY_TOKEN_BYTES
return|;
block|}
annotation|@
name|Override
DECL|method|extractCategoryTokenData
specifier|public
name|Object
name|extractCategoryTokenData
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|!=
name|CATEGORY_TOKEN_BYTES
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unexpected data length "
operator|+
name|length
argument_list|)
throw|;
block|}
name|byte
index|[]
name|ret
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|ret
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getRetainableProperty
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|CategoryProperty
argument_list|>
name|getRetainableProperty
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|CategoryEnhancementDummy3
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
