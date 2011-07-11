begin_unit
begin_package
DECL|package|org.apache.lucene.xmlparser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
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
name|Analyzer
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|xmlparser
operator|.
name|builders
operator|.
name|BooleanFilterBuilder
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
name|xmlparser
operator|.
name|builders
operator|.
name|BoostingQueryBuilder
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
name|xmlparser
operator|.
name|builders
operator|.
name|DuplicateFilterBuilder
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
name|xmlparser
operator|.
name|builders
operator|.
name|FuzzyLikeThisQueryBuilder
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
name|xmlparser
operator|.
name|builders
operator|.
name|LikeThisQueryBuilder
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
name|xmlparser
operator|.
name|builders
operator|.
name|TermsFilterBuilder
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *   */
end_comment
begin_class
DECL|class|CorePlusExtensionsParser
specifier|public
class|class
name|CorePlusExtensionsParser
extends|extends
name|CoreParser
block|{
comment|/** 	 * Construct an XML parser that uses a single instance QueryParser for handling  	 * UserQuery tags - all parse operations are synchronized on this parser 	 * @param analyzer 	 * @param parser A QueryParser which will be synchronized on during parse calls. 	 */
DECL|method|CorePlusExtensionsParser
specifier|public
name|CorePlusExtensionsParser
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|QueryParser
name|parser
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|analyzer
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructs an XML parser that creates a QueryParser for each UserQuery request. 	 * @param defaultField The default field name used by QueryParsers constructed for UserQuery tags  	 * @param analyzer  	 */
DECL|method|CorePlusExtensionsParser
specifier|public
name|CorePlusExtensionsParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|CorePlusExtensionsParser
specifier|private
name|CorePlusExtensionsParser
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|QueryParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"TermsFilter"
argument_list|,
operator|new
name|TermsFilterBuilder
argument_list|(
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"BooleanFilter"
argument_list|,
operator|new
name|BooleanFilterBuilder
argument_list|(
name|filterFactory
argument_list|)
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|addBuilder
argument_list|(
literal|"DuplicateFilter"
argument_list|,
operator|new
name|DuplicateFilterBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fields
index|[]
init|=
block|{
literal|"contents"
block|}
decl_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"LikeThisQuery"
argument_list|,
operator|new
name|LikeThisQueryBuilder
argument_list|(
name|analyzer
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"BoostingQuery"
argument_list|,
operator|new
name|BoostingQueryBuilder
argument_list|(
name|queryFactory
argument_list|)
argument_list|)
expr_stmt|;
name|queryFactory
operator|.
name|addBuilder
argument_list|(
literal|"FuzzyLikeThisQuery"
argument_list|,
operator|new
name|FuzzyLikeThisQueryBuilder
argument_list|(
name|analyzer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
