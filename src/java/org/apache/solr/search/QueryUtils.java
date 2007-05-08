begin_unit
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
name|MatchAllDocsQuery
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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|QueryUtils
specifier|public
class|class
name|QueryUtils
block|{
comment|/** return true if this query has no positive components */
DECL|method|isNegative
specifier|static
name|boolean
name|isNegative
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|bq
operator|.
name|clauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/** Returns the original query if it was already a positive query, otherwise    * return the negative of the query (i.e., a positive query).    *<p>    * Example: both id:10 and id:-10 will return id:10    *<p>    * The caller can tell the sign of the original by a reference comparison between    * the original and returned query.    * @param q    * @return    */
DECL|method|getAbs
specifier|static
name|Query
name|getAbs
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|BooleanQuery
operator|)
condition|)
return|return
name|q
return|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|bq
operator|.
name|clauses
argument_list|()
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|q
return|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
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
return|return
name|q
return|;
block|}
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// if only one clause, dispense with the wrapping BooleanQuery
name|Query
name|negClause
init|=
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// we shouldn't need to worry about adjusting the boosts since the negative
comment|// clause would have never been selected in a positive query, and hence would
comment|// not contribute to a score.
return|return
name|negClause
return|;
block|}
else|else
block|{
name|BooleanQuery
name|newBq
init|=
operator|new
name|BooleanQuery
argument_list|(
name|bq
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
decl_stmt|;
name|newBq
operator|.
name|setBoost
argument_list|(
name|bq
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|// ignore minNrShouldMatch... it doesn't make sense for a negative query
comment|// the inverse of -a -b is a OR b
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|newBq
operator|.
name|add
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|newBq
return|;
block|}
comment|/*** TODO: use after next lucene update     List<BooleanClause> clauses = (List<BooleanClause>)bq.clauses();     // A single filtered out stopword currently causes a BooleanQuery with     // zero clauses.     if (clauses.size()==0) return q;      for (BooleanClause clause: clauses) {       if (!clause.isProhibited()) return q;     }      if (clauses.size()==1) {       // if only one clause, dispense with the wrapping BooleanQuery       Query negClause = clauses.get(0).getQuery();       // we shouldn't need to worry about adjusting the boosts since the negative       // clause would have never been selected in a positive query, and hence the       // boost is meaningless.       return negClause;     } else {       BooleanQuery newBq = new BooleanQuery(bq.isCoordDisabled());       newBq.setBoost(bq.getBoost());       // ignore minNrShouldMatch... it doesn't make sense for a negative query        // the inverse of -a -b is a b       for (BooleanClause clause: clauses) {         newBq.add(clause.getQuery(), BooleanClause.Occur.SHOULD);       }       return newBq;     }     ***/
block|}
comment|/** Makes negative queries suitable for querying by    * lucene.    */
DECL|method|makeQueryable
specifier|static
name|Query
name|makeQueryable
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
return|return
name|isNegative
argument_list|(
name|q
argument_list|)
condition|?
name|fixNegativeQuery
argument_list|(
name|q
argument_list|)
else|:
name|q
return|;
block|}
comment|/** Fixes a negative query by adding a MatchAllDocs query clause.    * The query passed in *must* be a negative query.    */
DECL|method|fixNegativeQuery
specifier|static
name|Query
name|fixNegativeQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|BooleanQuery
name|newBq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
operator|.
name|clone
argument_list|()
decl_stmt|;
name|newBq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|newBq
return|;
block|}
block|}
end_class
end_unit
