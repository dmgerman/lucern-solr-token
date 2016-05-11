begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|search
operator|.
name|Query
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import
begin_comment
comment|/**  * Custom field type that overrides the prefix query behavior to map "X*" to [X TO Integer.MAX_VALUE].  * * This is used for testing overridden prefix query for custom fields in TestOverriddenPrefixQueryForCustomFieldType  */
end_comment
begin_class
DECL|class|TrieIntPrefixActsAsRangeQueryFieldType
specifier|public
class|class
name|TrieIntPrefixActsAsRangeQueryFieldType
extends|extends
name|TrieIntField
block|{
DECL|method|getPrefixQuery
specifier|public
name|Query
name|getPrefixQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|sf
parameter_list|,
name|String
name|termStr
parameter_list|)
block|{
return|return
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|,
name|termStr
argument_list|,
operator|new
name|String
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|""
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class
end_unit
