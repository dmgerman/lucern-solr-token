begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|query
package|;
end_package
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
name|Map
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchema
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|DateTime
import|;
end_import
begin_comment
comment|/**  * Simple static methods to translate the http query to a lucene query string.  * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|QueryTranslator
specifier|public
class|class
name|QueryTranslator
block|{
DECL|field|STANDARD_REQUEST_PARAMETER
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|STANDARD_REQUEST_PARAMETER
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
DECL|field|GDATA_DEFAULT_SEARCH_PARAMETER
specifier|private
specifier|static
specifier|final
name|String
name|GDATA_DEFAULT_SEARCH_PARAMETER
init|=
literal|"q"
decl_stmt|;
DECL|field|UPDATED_MIN
specifier|private
specifier|static
specifier|final
name|String
name|UPDATED_MIN
init|=
name|Long
operator|.
name|toString
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|UPDATED_MAX
specifier|private
specifier|static
specifier|final
name|String
name|UPDATED_MAX
init|=
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|//    private static final String GDATA_CATEGORY_FIEL =
static|static
block|{
name|STANDARD_REQUEST_PARAMETER
operator|.
name|add
argument_list|(
literal|"max-results"
argument_list|)
expr_stmt|;
name|STANDARD_REQUEST_PARAMETER
operator|.
name|add
argument_list|(
literal|"start-index"
argument_list|)
expr_stmt|;
name|STANDARD_REQUEST_PARAMETER
operator|.
name|add
argument_list|(
literal|"alt"
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method does a little preprocessing of the query. Basically it will map the given request parameters to a lucene syntax. Each      * parameter matching a index field in the given schema will be translated into a grouped query string according to the lucene query syntax.       *<p>      *<ol>      *<li>title=foo bar AND "FooBar" will be title:(foo bar AND "FooBar)</i>      *<li>updated-min=2005-08-09T10:57:00-08:00 will be translated to updated:[1123613820000 TO 9223372036854775807] according to the gdata protocol</i>      *</ol>      *</p>      * @param schema the index schema for the queried service      * @param parameterMap - the http parameter map returning String[] instances as values      * @param categoryQuery - the parsed category query from the request      * @return - a lucene syntax query string      */
DECL|method|translateHttpSearchRequest
specifier|public
specifier|static
name|String
name|translateHttpSearchRequest
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|parameterMap
parameter_list|,
name|String
name|categoryQuery
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|searchableFieldNames
init|=
name|schema
operator|.
name|getSearchableFieldNames
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|parameterSet
init|=
name|parameterMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|StringBuilder
name|translatedQuery
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|categoryQuery
operator|!=
literal|null
condition|)
block|{
name|translatedQuery
operator|.
name|append
argument_list|(
name|translateCategory
argument_list|(
name|translatedQuery
argument_list|,
name|categoryQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|updateMin
init|=
literal|null
decl_stmt|;
name|String
name|updateMax
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|parameterName
range|:
name|parameterSet
control|)
block|{
if|if
condition|(
name|STANDARD_REQUEST_PARAMETER
operator|.
name|contains
argument_list|(
name|parameterName
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|searchableFieldNames
operator|.
name|contains
argument_list|(
name|parameterName
argument_list|)
condition|)
block|{
name|translatedQuery
operator|.
name|append
argument_list|(
name|parameterName
argument_list|)
operator|.
name|append
argument_list|(
literal|":("
argument_list|)
expr_stmt|;
name|translatedQuery
operator|.
name|append
argument_list|(
name|parameterMap
operator|.
name|get
argument_list|(
name|parameterName
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|translatedQuery
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|parameterName
operator|.
name|equals
argument_list|(
name|GDATA_DEFAULT_SEARCH_PARAMETER
argument_list|)
condition|)
block|{
name|translatedQuery
operator|.
name|append
argument_list|(
name|schema
operator|.
name|getDefaultSearchField
argument_list|()
argument_list|)
expr_stmt|;
name|translatedQuery
operator|.
name|append
argument_list|(
literal|":("
argument_list|)
expr_stmt|;
name|translatedQuery
operator|.
name|append
argument_list|(
name|parameterMap
operator|.
name|get
argument_list|(
name|parameterName
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|translatedQuery
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|parameterName
operator|.
name|endsWith
argument_list|(
literal|"updated-min"
argument_list|)
condition|)
block|{
name|updateMin
operator|=
name|parameterMap
operator|.
name|get
argument_list|(
name|parameterName
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|parameterName
operator|.
name|endsWith
argument_list|(
literal|"updated-max"
argument_list|)
condition|)
block|{
name|updateMax
operator|=
name|parameterMap
operator|.
name|get
argument_list|(
name|parameterName
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not apply parameter -- invalid -- "
operator|+
name|parameterName
argument_list|)
throw|;
block|}
if|if
condition|(
name|updateMax
operator|!=
literal|null
operator|||
name|updateMin
operator|!=
literal|null
condition|)
name|translatedQuery
operator|.
name|append
argument_list|(
name|translateUpdate
argument_list|(
name|updateMin
argument_list|,
name|updateMax
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|translatedQuery
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|translatedQuery
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|translateUpdate
specifier|static
name|String
name|translateUpdate
parameter_list|(
name|String
name|updateMin
parameter_list|,
name|String
name|updateMax
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"updated:["
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateMin
operator|!=
literal|null
condition|)
name|builder
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|DateTime
operator|.
name|parseDateTime
argument_list|(
name|updateMin
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|builder
operator|.
name|append
argument_list|(
name|UPDATED_MIN
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateMax
operator|!=
literal|null
condition|)
name|builder
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|DateTime
operator|.
name|parseDateTime
argument_list|(
name|updateMax
argument_list|)
operator|.
name|getValue
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|builder
operator|.
name|append
argument_list|(
name|UPDATED_MAX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|translateCategory
specifier|static
name|String
name|translateCategory
parameter_list|(
name|StringBuilder
name|builder
parameter_list|,
name|String
name|categoryQuery
parameter_list|)
block|{
return|return
name|categoryQuery
return|;
comment|//TODO Implement this
comment|//         GDataCategoryQueryParser parser = new GDataCategoryQueryParser()
block|}
block|}
end_class
end_unit
