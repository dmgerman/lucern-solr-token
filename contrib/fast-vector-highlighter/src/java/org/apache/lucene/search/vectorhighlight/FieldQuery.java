begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Iterator
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
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|DisjunctionMaxQuery
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
name|search
operator|.
name|PhraseQuery
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
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldTermStack
operator|.
name|TermInfo
import|;
end_import
begin_comment
comment|/**  * FieldQuery breaks down query object into terms/phrases and keep  * them in QueryPhraseMap structure.  */
end_comment
begin_class
DECL|class|FieldQuery
specifier|public
class|class
name|FieldQuery
block|{
DECL|field|fieldMatch
specifier|final
name|boolean
name|fieldMatch
decl_stmt|;
comment|// fieldMatch==true,  Map<fieldName,QueryPhraseMap>
comment|// fieldMatch==false, Map<null,QueryPhraseMap>
DECL|field|rootMaps
name|Map
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
name|rootMaps
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
argument_list|()
decl_stmt|;
comment|// fieldMatch==true,  Map<fieldName,setOfTermsInQueries>
comment|// fieldMatch==false, Map<null,setOfTermsInQueries>
DECL|field|termSetMap
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|termSetMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|termOrPhraseNumber
name|int
name|termOrPhraseNumber
decl_stmt|;
comment|// used for colored tag support
DECL|method|FieldQuery
name|FieldQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|boolean
name|phraseHighlight
parameter_list|,
name|boolean
name|fieldMatch
parameter_list|)
block|{
name|this
operator|.
name|fieldMatch
operator|=
name|fieldMatch
expr_stmt|;
name|Set
argument_list|<
name|Query
argument_list|>
name|flatQueries
init|=
operator|new
name|HashSet
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|flatten
argument_list|(
name|query
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
name|saveTerms
argument_list|(
name|flatQueries
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Query
argument_list|>
name|expandQueries
init|=
name|expand
argument_list|(
name|flatQueries
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|flatQuery
range|:
name|expandQueries
control|)
block|{
name|QueryPhraseMap
name|rootMap
init|=
name|getRootMap
argument_list|(
name|flatQuery
argument_list|)
decl_stmt|;
name|rootMap
operator|.
name|add
argument_list|(
name|flatQuery
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|phraseHighlight
operator|&&
name|flatQuery
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|PhraseQuery
name|pq
init|=
operator|(
name|PhraseQuery
operator|)
name|flatQuery
decl_stmt|;
if|if
condition|(
name|pq
operator|.
name|getTerms
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|Term
name|term
range|:
name|pq
operator|.
name|getTerms
argument_list|()
control|)
name|rootMap
operator|.
name|addTerm
argument_list|(
name|term
argument_list|,
name|flatQuery
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|flatten
name|void
name|flatten
parameter_list|(
name|Query
name|sourceQuery
parameter_list|,
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
block|{
if|if
condition|(
name|sourceQuery
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|sourceQuery
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
operator|.
name|getClauses
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|clause
operator|.
name|isProhibited
argument_list|()
condition|)
name|flatten
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|DisjunctionMaxQuery
condition|)
block|{
name|DisjunctionMaxQuery
name|dmq
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|sourceQuery
decl_stmt|;
for|for
control|(
name|Query
name|query
range|:
name|dmq
control|)
block|{
name|flatten
argument_list|(
name|query
argument_list|,
name|flatQueries
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|TermQuery
condition|)
block|{
if|if
condition|(
operator|!
name|flatQueries
operator|.
name|contains
argument_list|(
name|sourceQuery
argument_list|)
condition|)
name|flatQueries
operator|.
name|add
argument_list|(
name|sourceQuery
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceQuery
operator|instanceof
name|PhraseQuery
condition|)
block|{
if|if
condition|(
operator|!
name|flatQueries
operator|.
name|contains
argument_list|(
name|sourceQuery
argument_list|)
condition|)
block|{
name|PhraseQuery
name|pq
init|=
operator|(
name|PhraseQuery
operator|)
name|sourceQuery
decl_stmt|;
if|if
condition|(
name|pq
operator|.
name|getTerms
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
name|flatQueries
operator|.
name|add
argument_list|(
name|pq
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|pq
operator|.
name|getTerms
argument_list|()
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|flatQueries
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|pq
operator|.
name|getTerms
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// else discard queries
block|}
comment|/*    * Create expandQueries from flatQueries.    *     * expandQueries := flatQueries + overlapped phrase queries    *     * ex1) flatQueries={a,b,c}    *      => expandQueries={a,b,c}    * ex2) flatQueries={a,"b c","c d"}    *      => expandQueries={a,"b c","c d","b c d"}    */
DECL|method|expand
name|Collection
argument_list|<
name|Query
argument_list|>
name|expand
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
block|{
name|Set
argument_list|<
name|Query
argument_list|>
name|expandQueries
init|=
operator|new
name|HashSet
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Query
argument_list|>
name|i
init|=
name|flatQueries
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Query
name|query
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|expandQueries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|query
operator|instanceof
name|PhraseQuery
operator|)
condition|)
continue|continue;
for|for
control|(
name|Iterator
argument_list|<
name|Query
argument_list|>
name|j
init|=
name|flatQueries
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Query
name|qj
init|=
name|j
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|qj
operator|instanceof
name|PhraseQuery
operator|)
condition|)
continue|continue;
name|checkOverlap
argument_list|(
name|expandQueries
argument_list|,
operator|(
name|PhraseQuery
operator|)
name|query
argument_list|,
operator|(
name|PhraseQuery
operator|)
name|qj
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|expandQueries
return|;
block|}
comment|/*    * Check if PhraseQuery A and B have overlapped part.    *     * ex1) A="a b", B="b c" => overlap; expandQueries={"a b c"}    * ex2) A="b c", B="a b" => overlap; expandQueries={"a b c"}    * ex3) A="a b", B="c d" => no overlap; expandQueries={}    */
DECL|method|checkOverlap
specifier|private
name|void
name|checkOverlap
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|expandQueries
parameter_list|,
name|PhraseQuery
name|a
parameter_list|,
name|PhraseQuery
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|getSlop
argument_list|()
operator|!=
name|b
operator|.
name|getSlop
argument_list|()
condition|)
return|return;
name|Term
index|[]
name|ats
init|=
name|a
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|Term
index|[]
name|bts
init|=
name|b
operator|.
name|getTerms
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldMatch
operator|&&
operator|!
name|ats
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|bts
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|)
condition|)
return|return;
name|checkOverlap
argument_list|(
name|expandQueries
argument_list|,
name|ats
argument_list|,
name|bts
argument_list|,
name|a
operator|.
name|getSlop
argument_list|()
argument_list|,
name|a
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|checkOverlap
argument_list|(
name|expandQueries
argument_list|,
name|bts
argument_list|,
name|ats
argument_list|,
name|b
operator|.
name|getSlop
argument_list|()
argument_list|,
name|b
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Check if src and dest have overlapped part and if it is, create PhraseQueries and add expandQueries.    *     * ex1) src="a b", dest="c d"       => no overlap    * ex2) src="a b", dest="a b c"     => no overlap    * ex3) src="a b", dest="b c"       => overlap; expandQueries={"a b c"}    * ex4) src="a b c", dest="b c d"   => overlap; expandQueries={"a b c d"}    * ex5) src="a b c", dest="b c"     => no overlap    * ex6) src="a b c", dest="b"       => no overlap    * ex7) src="a a a a", dest="a a a" => overlap;    *                                     expandQueries={"a a a a a","a a a a a a"}    * ex8) src="a b c d", dest="b c"   => no overlap    */
DECL|method|checkOverlap
specifier|private
name|void
name|checkOverlap
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|expandQueries
parameter_list|,
name|Term
index|[]
name|src
parameter_list|,
name|Term
index|[]
name|dest
parameter_list|,
name|int
name|slop
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|// beginning from 1 (not 0) is safe because that the PhraseQuery has multiple terms
comment|// is guaranteed in flatten() method (if PhraseQuery has only one term, flatten()
comment|// converts PhraseQuery to TermQuery)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|src
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|overlap
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|src
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|j
operator|-
name|i
operator|)
operator|<
name|dest
operator|.
name|length
operator|&&
operator|!
name|src
index|[
name|j
index|]
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
name|dest
index|[
name|j
operator|-
name|i
index|]
operator|.
name|text
argument_list|()
argument_list|)
condition|)
block|{
name|overlap
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|overlap
operator|&&
name|src
operator|.
name|length
operator|-
name|i
operator|<
name|dest
operator|.
name|length
condition|)
block|{
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|srcTerm
range|:
name|src
control|)
name|pq
operator|.
name|add
argument_list|(
name|srcTerm
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
name|src
operator|.
name|length
operator|-
name|i
init|;
name|k
operator|<
name|dest
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|src
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|,
name|dest
index|[
name|k
index|]
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pq
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|pq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expandQueries
operator|.
name|contains
argument_list|(
name|pq
argument_list|)
condition|)
name|expandQueries
operator|.
name|add
argument_list|(
name|pq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRootMap
name|QueryPhraseMap
name|getRootMap
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|String
name|key
init|=
name|getKey
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|QueryPhraseMap
name|map
init|=
name|rootMaps
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|QueryPhraseMap
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|rootMaps
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/*    * Return 'key' string. 'key' is the field name of the Query.    * If not fieldMatch, 'key' will be null.    */
DECL|method|getKey
specifier|private
name|String
name|getKey
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fieldMatch
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
return|return
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
return|;
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|PhraseQuery
name|pq
init|=
operator|(
name|PhraseQuery
operator|)
name|query
decl_stmt|;
name|Term
index|[]
name|terms
init|=
name|pq
operator|.
name|getTerms
argument_list|()
decl_stmt|;
return|return
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
return|;
block|}
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"query \""
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|"\" must be flatten first."
argument_list|)
throw|;
block|}
comment|/*    * Save the set of terms in the queries to termSetMap.    *     * ex1) q=name:john    *      - fieldMatch==true    *          termSetMap=Map<"name",Set<"john">>    *      - fieldMatch==false    *          termSetMap=Map<null,Set<"john">>    *              * ex2) q=name:john title:manager    *      - fieldMatch==true    *          termSetMap=Map<"name",Set<"john">,    *                         "title",Set<"manager">>    *      - fieldMatch==false    *          termSetMap=Map<null,Set<"john","manager">>    *              * ex3) q=name:"john lennon"    *      - fieldMatch==true    *          termSetMap=Map<"name",Set<"john","lennon">>    *      - fieldMatch==false    *          termSetMap=Map<null,Set<"john","lennon">>    */
DECL|method|saveTerms
name|void
name|saveTerms
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|flatQueries
parameter_list|)
block|{
for|for
control|(
name|Query
name|query
range|:
name|flatQueries
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|termSet
init|=
name|getTermSet
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
name|termSet
operator|.
name|add
argument_list|(
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
for|for
control|(
name|Term
name|term
range|:
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|getTerms
argument_list|()
control|)
name|termSet
operator|.
name|add
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"query \""
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|"\" must be flatten first."
argument_list|)
throw|;
block|}
block|}
DECL|method|getTermSet
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getTermSet
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|String
name|key
init|=
name|getKey
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|termSetMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|termSetMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|getTermSet
name|Set
argument_list|<
name|String
argument_list|>
name|getTermSet
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|termSetMap
operator|.
name|get
argument_list|(
name|fieldMatch
condition|?
name|field
else|:
literal|null
argument_list|)
return|;
block|}
comment|/**    *     * @param fieldName    * @param term    * @return QueryPhraseMap    */
DECL|method|getFieldTermMap
specifier|public
name|QueryPhraseMap
name|getFieldTermMap
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|QueryPhraseMap
name|rootMap
init|=
name|getRootMap
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
name|rootMap
operator|==
literal|null
condition|?
literal|null
else|:
name|rootMap
operator|.
name|subMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/**    *     * @param fieldName    * @param phraseCandidate    * @return QueryPhraseMap    */
DECL|method|searchPhrase
specifier|public
name|QueryPhraseMap
name|searchPhrase
parameter_list|(
name|String
name|fieldName
parameter_list|,
specifier|final
name|List
argument_list|<
name|TermInfo
argument_list|>
name|phraseCandidate
parameter_list|)
block|{
name|QueryPhraseMap
name|root
init|=
name|getRootMap
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|root
operator|.
name|searchPhrase
argument_list|(
name|phraseCandidate
argument_list|)
return|;
block|}
DECL|method|getRootMap
specifier|private
name|QueryPhraseMap
name|getRootMap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|rootMaps
operator|.
name|get
argument_list|(
name|fieldMatch
condition|?
name|fieldName
else|:
literal|null
argument_list|)
return|;
block|}
DECL|method|nextTermOrPhraseNumber
name|int
name|nextTermOrPhraseNumber
parameter_list|()
block|{
return|return
name|termOrPhraseNumber
operator|++
return|;
block|}
DECL|class|QueryPhraseMap
specifier|public
specifier|static
class|class
name|QueryPhraseMap
block|{
DECL|field|terminal
name|boolean
name|terminal
decl_stmt|;
DECL|field|slop
name|int
name|slop
decl_stmt|;
comment|// valid if terminal == true and phraseHighlight == true
DECL|field|boost
name|float
name|boost
decl_stmt|;
comment|// valid if terminal == true
DECL|field|termOrPhraseNumber
name|int
name|termOrPhraseNumber
decl_stmt|;
comment|// valid if terminal == true
DECL|field|fieldQuery
name|FieldQuery
name|fieldQuery
decl_stmt|;
DECL|field|subMap
name|Map
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
name|subMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|QueryPhraseMap
specifier|public
name|QueryPhraseMap
parameter_list|(
name|FieldQuery
name|fieldQuery
parameter_list|)
block|{
name|this
operator|.
name|fieldQuery
operator|=
name|fieldQuery
expr_stmt|;
block|}
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|QueryPhraseMap
name|map
init|=
name|getOrNewMap
argument_list|(
name|subMap
argument_list|,
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|map
operator|.
name|markTerminal
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|getOrNewMap
specifier|private
name|QueryPhraseMap
name|getOrNewMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
name|subMap
parameter_list|,
name|String
name|term
parameter_list|)
block|{
name|QueryPhraseMap
name|map
init|=
name|subMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|QueryPhraseMap
argument_list|(
name|fieldQuery
argument_list|)
expr_stmt|;
name|subMap
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|TermQuery
condition|)
block|{
name|addTerm
argument_list|(
operator|(
operator|(
name|TermQuery
operator|)
name|query
operator|)
operator|.
name|getTerm
argument_list|()
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
name|PhraseQuery
name|pq
init|=
operator|(
name|PhraseQuery
operator|)
name|query
decl_stmt|;
name|Term
index|[]
name|terms
init|=
name|pq
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|QueryPhraseMap
argument_list|>
name|map
init|=
name|subMap
decl_stmt|;
name|QueryPhraseMap
name|qpm
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|qpm
operator|=
name|getOrNewMap
argument_list|(
name|map
argument_list|,
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|=
name|qpm
operator|.
name|subMap
expr_stmt|;
block|}
name|qpm
operator|.
name|markTerminal
argument_list|(
name|pq
operator|.
name|getSlop
argument_list|()
argument_list|,
name|pq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"query \""
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|"\" must be flatten first."
argument_list|)
throw|;
block|}
DECL|method|getTermMap
specifier|public
name|QueryPhraseMap
name|getTermMap
parameter_list|(
name|String
name|term
parameter_list|)
block|{
return|return
name|subMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|markTerminal
specifier|private
name|void
name|markTerminal
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|markTerminal
argument_list|(
literal|0
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|markTerminal
specifier|private
name|void
name|markTerminal
parameter_list|(
name|int
name|slop
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|terminal
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|termOrPhraseNumber
operator|=
name|fieldQuery
operator|.
name|nextTermOrPhraseNumber
argument_list|()
expr_stmt|;
block|}
DECL|method|isTerminal
specifier|public
name|boolean
name|isTerminal
parameter_list|()
block|{
return|return
name|terminal
return|;
block|}
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
DECL|method|getTermOrPhraseNumber
specifier|public
name|int
name|getTermOrPhraseNumber
parameter_list|()
block|{
return|return
name|termOrPhraseNumber
return|;
block|}
DECL|method|searchPhrase
specifier|public
name|QueryPhraseMap
name|searchPhrase
parameter_list|(
specifier|final
name|List
argument_list|<
name|TermInfo
argument_list|>
name|phraseCandidate
parameter_list|)
block|{
name|QueryPhraseMap
name|currMap
init|=
name|this
decl_stmt|;
for|for
control|(
name|TermInfo
name|ti
range|:
name|phraseCandidate
control|)
block|{
name|currMap
operator|=
name|currMap
operator|.
name|subMap
operator|.
name|get
argument_list|(
name|ti
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|currMap
operator|==
literal|null
condition|)
return|return
literal|null
return|;
block|}
return|return
name|currMap
operator|.
name|isValidTermOrPhrase
argument_list|(
name|phraseCandidate
argument_list|)
condition|?
name|currMap
else|:
literal|null
return|;
block|}
DECL|method|isValidTermOrPhrase
specifier|public
name|boolean
name|isValidTermOrPhrase
parameter_list|(
specifier|final
name|List
argument_list|<
name|TermInfo
argument_list|>
name|phraseCandidate
parameter_list|)
block|{
comment|// check terminal
if|if
condition|(
operator|!
name|terminal
condition|)
return|return
literal|false
return|;
comment|// if the candidate is a term, it is valid
if|if
condition|(
name|phraseCandidate
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
return|return
literal|true
return|;
comment|// else check whether the candidate is valid phrase
comment|// compare position-gaps between terms to slop
name|int
name|pos
init|=
name|phraseCandidate
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPosition
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|phraseCandidate
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|nextPos
init|=
name|phraseCandidate
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|nextPos
operator|-
name|pos
operator|-
literal|1
argument_list|)
operator|>
name|slop
condition|)
return|return
literal|false
return|;
name|pos
operator|=
name|nextPos
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
