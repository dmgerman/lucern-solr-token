begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|*
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
name|function
operator|.
name|*
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
name|queryParser
operator|.
name|ParseException
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|Term
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|SolrException
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|FieldType
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
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
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|QueryParsing
specifier|public
class|class
name|QueryParsing
block|{
DECL|method|parseQuery
specifier|public
specifier|static
name|Query
name|parseQuery
parameter_list|(
name|String
name|qs
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
return|return
name|parseQuery
argument_list|(
name|qs
argument_list|,
literal|null
argument_list|,
name|schema
argument_list|)
return|;
block|}
DECL|method|parseQuery
specifier|public
specifier|static
name|Query
name|parseQuery
parameter_list|(
name|String
name|qs
parameter_list|,
name|String
name|defaultField
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
try|try
block|{
name|Query
name|query
init|=
operator|new
name|SolrQueryParser
argument_list|(
name|schema
argument_list|,
name|defaultField
argument_list|)
operator|.
name|parse
argument_list|(
name|qs
argument_list|)
decl_stmt|;
if|if
condition|(
name|SolrCore
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|finest
argument_list|(
literal|"After QueryParser:"
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|SolrCore
operator|.
name|log
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"Error parsing Lucene query"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/***    * SortSpec encapsulates a Lucene Sort and a count of the number of documents    * to return.    */
DECL|class|SortSpec
specifier|public
specifier|static
class|class
name|SortSpec
block|{
DECL|field|sort
specifier|private
specifier|final
name|Sort
name|sort
decl_stmt|;
DECL|field|num
specifier|private
specifier|final
name|int
name|num
decl_stmt|;
DECL|method|SortSpec
name|SortSpec
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|num
operator|=
name|num
expr_stmt|;
block|}
comment|/**      * Gets the Lucene Sort object, or null for the default sort      * by score descending.      */
DECL|method|getSort
specifier|public
name|Sort
name|getSort
parameter_list|()
block|{
return|return
name|sort
return|;
block|}
comment|/**      * Gets the number of documens to return after sorting.      *      * @return number of docs to return, or -1 for no cut off (just sort)      */
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|num
return|;
block|}
block|}
DECL|field|sortSeparator
specifier|private
specifier|static
name|Pattern
name|sortSeparator
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\\s,]+"
argument_list|)
decl_stmt|;
comment|/**    * Returns null if the sortSpec string doesn't look like a sort specification,    * or if the sort specification couldn't be converted into a Lucene Sort    * (because of a field not being indexed or undefined, etc).    *    * The form of the sort specification string currently parsed is:    * SortSpec ::= SingleSort [, SingleSort]*<number>?    * SingleSort ::=<fieldname> SortDirection    * SortDirection ::= top | desc | bottom | asc    *    * Examples:    *   top 10                        #take the top 10 by score    *   desc 10                       #take the top 10 by score    *   score desc 10                 #take the top 10 by score    *   weight bottom 10              #sort by weight ascending and take the first 10    *   weight desc                   #sort by weight descending    *   height desc,weight desc       #sort by height descending, and use weight descending to break any ties    *   height desc,weight asc top 20 #sort by height descending, using weight ascending as a tiebreaker    *    */
DECL|method|parseSort
specifier|public
specifier|static
name|SortSpec
name|parseSort
parameter_list|(
name|String
name|sortSpec
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|sortSpec
operator|==
literal|null
operator|||
name|sortSpec
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// I wonder how fast the regex is??? as least we cache the pattern.
name|String
index|[]
name|parts
init|=
name|sortSeparator
operator|.
name|split
argument_list|(
name|sortSpec
operator|.
name|trim
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|ArrayList
argument_list|<
name|SortField
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|String
name|fn
decl_stmt|;
name|boolean
name|top
init|=
literal|true
decl_stmt|;
name|boolean
name|normalSortOnScore
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|parts
operator|.
name|length
condition|)
block|{
name|String
name|str
init|=
name|parts
index|[
name|pos
index|]
decl_stmt|;
if|if
condition|(
literal|"top"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
operator|||
literal|"bottom"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
operator|||
literal|"asc"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
operator|||
literal|"desc"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
condition|)
block|{
comment|// if the field name seems to be missing, default to "score".
comment|// note that this will mess up a field name that has the same name
comment|// as a sort direction specifier.
name|fn
operator|=
literal|"score"
expr_stmt|;
block|}
else|else
block|{
name|fn
operator|=
name|str
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
comment|// get the direction of the sort
name|str
operator|=
name|parts
index|[
name|pos
index|]
expr_stmt|;
if|if
condition|(
literal|"top"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
operator|||
literal|"desc"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
condition|)
block|{
name|top
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"bottom"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
operator|||
literal|"asc"
operator|.
name|equals
argument_list|(
name|str
argument_list|)
condition|)
block|{
name|top
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
comment|// must not be a sort command
block|}
comment|// get the field to sort on
comment|// hmmm - should there be a fake/pseudo-field named "score" in the schema?
if|if
condition|(
literal|"score"
operator|.
name|equals
argument_list|(
name|fn
argument_list|)
condition|)
block|{
if|if
condition|(
name|top
condition|)
block|{
name|normalSortOnScore
operator|=
literal|true
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lst
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|SCORE
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// getField could throw an exception if the name isn't found
try|try
block|{
name|SchemaField
name|f
init|=
name|schema
operator|.
name|getField
argument_list|(
name|fn
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
operator|||
operator|!
name|f
operator|.
name|indexed
argument_list|()
condition|)
return|return
literal|null
return|;
name|lst
operator|.
name|add
argument_list|(
name|f
operator|.
name|getType
argument_list|()
operator|.
name|getSortField
argument_list|(
name|f
argument_list|,
name|top
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
name|pos
operator|++
expr_stmt|;
comment|// If there is a leftover part, assume it is a count
if|if
condition|(
name|pos
operator|+
literal|1
operator|==
name|parts
operator|.
name|length
condition|)
block|{
try|try
block|{
name|num
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
block|}
name|Sort
name|sort
decl_stmt|;
if|if
condition|(
name|normalSortOnScore
operator|&&
name|lst
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// Normalize the default sort on score descending to sort=null
name|sort
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
operator|new
name|Sort
argument_list|(
operator|(
name|SortField
index|[]
operator|)
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|SortField
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SortSpec
argument_list|(
name|sort
argument_list|,
name|num
argument_list|)
return|;
block|}
comment|///////////////////////////
comment|///////////////////////////
comment|///////////////////////////
DECL|method|writeFieldName
specifier|static
name|FieldType
name|writeFieldName
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldType
name|ft
init|=
literal|null
decl_stmt|;
name|ft
operator|=
name|schema
operator|.
name|getFieldTypeNoEx
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"(UNKNOWN FIELD "
operator|+
name|name
operator|+
literal|')'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
return|return
name|ft
return|;
block|}
DECL|method|writeFieldVal
specifier|static
name|void
name|writeFieldVal
parameter_list|(
name|String
name|val
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|ft
operator|.
name|toExternal
argument_list|(
operator|new
name|Field
argument_list|(
literal|""
argument_list|,
name|val
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
specifier|static
name|void
name|toString
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Appendable
name|out
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|writeBoost
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|TermQuery
name|q
init|=
operator|(
name|TermQuery
operator|)
name|query
decl_stmt|;
name|Term
name|t
init|=
name|q
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|writeFieldVal
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|RangeQuery
condition|)
block|{
name|RangeQuery
name|q
init|=
operator|(
name|RangeQuery
operator|)
name|query
decl_stmt|;
name|String
name|fname
init|=
name|q
operator|.
name|getField
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|fname
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|isInclusive
argument_list|()
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|Term
name|lt
init|=
name|q
operator|.
name|getLowerTerm
argument_list|()
decl_stmt|;
name|Term
name|ut
init|=
name|q
operator|.
name|getUpperTerm
argument_list|()
decl_stmt|;
if|if
condition|(
name|lt
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|lt
operator|.
name|text
argument_list|()
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|ut
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|ut
operator|.
name|text
argument_list|()
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|isInclusive
argument_list|()
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreRangeQuery
condition|)
block|{
name|ConstantScoreRangeQuery
name|q
init|=
operator|(
name|ConstantScoreRangeQuery
operator|)
name|query
decl_stmt|;
name|String
name|fname
init|=
name|q
operator|.
name|getField
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|fname
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesLower
argument_list|()
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
expr_stmt|;
name|String
name|lt
init|=
name|q
operator|.
name|getLowerVal
argument_list|()
decl_stmt|;
name|String
name|ut
init|=
name|q
operator|.
name|getUpperVal
argument_list|()
decl_stmt|;
if|if
condition|(
name|lt
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|lt
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
expr_stmt|;
if|if
condition|(
name|ut
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeFieldVal
argument_list|(
name|ut
argument_list|,
name|ft
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|q
operator|.
name|includesUpper
argument_list|()
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|q
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|boolean
name|needParens
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|q
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1.0
operator|||
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|needParens
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
name|BooleanClause
index|[]
name|clauses
init|=
name|q
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|BooleanClause
name|c
range|:
name|clauses
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|isProhibited
argument_list|()
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|.
name|isRequired
argument_list|()
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
name|Query
name|subQuery
init|=
name|c
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|boolean
name|wrapQuery
init|=
literal|false
decl_stmt|;
comment|// TODO: may need to put parens around other types
comment|// of queries too, depending on future syntax.
if|if
condition|(
name|subQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|wrapQuery
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|wrapQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
name|toString
argument_list|(
name|subQuery
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrapQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|needParens
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|q
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PrefixQuery
condition|)
block|{
name|PrefixQuery
name|q
init|=
operator|(
name|PrefixQuery
operator|)
name|query
decl_stmt|;
name|Term
name|prefix
init|=
name|q
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScorePrefixQuery
condition|)
block|{
name|ConstantScorePrefixQuery
name|q
init|=
operator|(
name|ConstantScorePrefixQuery
operator|)
name|query
decl_stmt|;
name|Term
name|prefix
init|=
name|q
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
name|writeFieldName
argument_list|(
name|prefix
operator|.
name|field
argument_list|()
argument_list|,
name|schema
argument_list|,
name|out
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|prefix
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|WildcardQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writeBoost
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writeBoost
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writeBoost
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|query
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'('
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|')'
argument_list|)
expr_stmt|;
name|writeBoost
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|writeBoost
operator|&&
name|query
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
try|try
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toString
argument_list|(
name|query
argument_list|,
name|schema
argument_list|,
name|sb
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|// simple class to help with parsing a string
DECL|class|StrParser
specifier|private
specifier|static
class|class
name|StrParser
block|{
DECL|field|val
name|String
name|val
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|end
name|int
name|end
decl_stmt|;
DECL|method|StrParser
name|StrParser
parameter_list|(
name|String
name|val
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
name|end
operator|=
name|val
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
DECL|method|eatws
name|void
name|eatws
parameter_list|()
block|{
while|while
condition|(
name|pos
operator|<
name|end
operator|&&
name|Character
operator|.
name|isWhitespace
argument_list|(
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
argument_list|)
condition|)
name|pos
operator|++
expr_stmt|;
block|}
DECL|method|opt
name|boolean
name|opt
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|regionMatches
argument_list|(
name|pos
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|)
condition|)
block|{
name|pos
operator|+=
name|slen
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|expect
name|void
name|expect
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ParseException
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|regionMatches
argument_list|(
name|pos
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|)
condition|)
block|{
name|pos
operator|+=
name|slen
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Expected '"
operator|+
name|s
operator|+
literal|"' at position "
operator|+
name|pos
operator|+
literal|" in '"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|getFloat
name|float
name|getFloat
parameter_list|()
throws|throws
name|ParseException
block|{
name|eatws
argument_list|()
expr_stmt|;
name|char
index|[]
name|arr
init|=
operator|new
name|char
index|[
name|end
operator|-
name|pos
index|]
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
operator|||
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'e'
operator|||
name|ch
operator|==
literal|'E'
condition|)
block|{
name|pos
operator|++
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
operator|new
name|String
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getId
name|String
name|getId
parameter_list|()
throws|throws
name|ParseException
block|{
name|eatws
argument_list|()
expr_stmt|;
name|int
name|id_start
init|=
name|pos
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
operator|&&
name|Character
operator|.
name|isJavaIdentifierPart
argument_list|(
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
argument_list|)
condition|)
name|pos
operator|++
expr_stmt|;
return|return
name|val
operator|.
name|substring
argument_list|(
name|id_start
argument_list|,
name|pos
argument_list|)
return|;
block|}
DECL|method|peek
name|char
name|peek
parameter_list|()
block|{
name|eatws
argument_list|()
expr_stmt|;
return|return
name|pos
operator|<
name|end
condition|?
name|val
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
else|:
literal|0
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"'"
operator|+
name|val
operator|+
literal|"'"
operator|+
literal|", pos="
operator|+
name|pos
return|;
block|}
block|}
DECL|method|parseValSource
specifier|private
specifier|static
name|ValueSource
name|parseValSource
parameter_list|(
name|StrParser
name|sp
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|id
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sp
operator|.
name|opt
argument_list|(
literal|"("
argument_list|)
condition|)
block|{
comment|// a function: could contain a fieldname or another function.
name|ValueSource
name|vs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"ord"
argument_list|)
condition|)
block|{
name|String
name|field
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
name|vs
operator|=
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"rord"
argument_list|)
condition|)
block|{
name|String
name|field
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
name|vs
operator|=
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"linear"
argument_list|)
condition|)
block|{
name|ValueSource
name|source
init|=
name|parseValSource
argument_list|(
name|sp
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|slope
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|intercept
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|vs
operator|=
operator|new
name|LinearFloatFunction
argument_list|(
name|source
argument_list|,
name|slope
argument_list|,
name|intercept
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"max"
argument_list|)
condition|)
block|{
name|ValueSource
name|source
init|=
name|parseValSource
argument_list|(
name|sp
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|val
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|vs
operator|=
operator|new
name|MaxFloatFunction
argument_list|(
name|source
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"recip"
argument_list|)
condition|)
block|{
name|ValueSource
name|source
init|=
name|parseValSource
argument_list|(
name|sp
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|m
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|a
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|float
name|b
init|=
name|sp
operator|.
name|getFloat
argument_list|()
decl_stmt|;
name|vs
operator|=
operator|new
name|ReciprocalFloatFunction
argument_list|(
name|source
argument_list|,
name|m
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unknown function "
operator|+
name|id
operator|+
literal|" in FunctionQuery("
operator|+
name|sp
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|sp
operator|.
name|expect
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|vs
return|;
block|}
name|SchemaField
name|f
init|=
name|schema
operator|.
name|getField
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** Parse a function, returning a FunctionQuery    */
DECL|method|parseFunction
specifier|public
specifier|static
name|FunctionQuery
name|parseFunction
parameter_list|(
name|String
name|func
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
operator|new
name|FunctionQuery
argument_list|(
name|parseValSource
argument_list|(
operator|new
name|StrParser
argument_list|(
name|func
argument_list|)
argument_list|,
name|schema
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
