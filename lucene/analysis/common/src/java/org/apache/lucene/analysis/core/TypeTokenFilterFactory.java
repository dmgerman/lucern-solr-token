begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
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
name|analysis
operator|.
name|core
operator|.
name|TypeTokenFilter
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoaderAware
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
name|analysis
operator|.
name|util
operator|.
name|TokenFilterFactory
import|;
end_import
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
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Factory class for {@link TypeTokenFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="chars" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.TypeTokenFilterFactory" types="stoptypes.txt"  *                   enablePositionIncrements="true" useWhitelist="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|TypeTokenFilterFactory
specifier|public
class|class
name|TypeTokenFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|stopTypesFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"types"
argument_list|)
decl_stmt|;
name|enablePositionIncrements
operator|=
name|getBoolean
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|useWhitelist
operator|=
name|getBoolean
argument_list|(
literal|"useWhitelist"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|stopTypesFiles
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|stopTypesFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|stopTypes
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|typesLines
init|=
name|getLines
argument_list|(
name|loader
argument_list|,
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|stopTypes
operator|.
name|addAll
argument_list|(
name|typesLines
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing required parameter: types."
argument_list|)
throw|;
block|}
block|}
DECL|field|useWhitelist
specifier|private
name|boolean
name|useWhitelist
decl_stmt|;
DECL|field|stopTypes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|stopTypes
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
decl_stmt|;
DECL|method|isEnablePositionIncrements
specifier|public
name|boolean
name|isEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
DECL|method|getStopTypes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getStopTypes
parameter_list|()
block|{
return|return
name|stopTypes
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|TypeTokenFilter
argument_list|(
name|enablePositionIncrements
argument_list|,
name|input
argument_list|,
name|stopTypes
argument_list|,
name|useWhitelist
argument_list|)
return|;
block|}
block|}
end_class
end_unit
