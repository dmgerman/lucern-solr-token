begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|IndexReader
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
operator|.
name|Occur
import|;
end_import
begin_comment
comment|/**  * A container Filter that allows Boolean composition of Filters.  * Filters are allocated into one of three logical constructs;  * SHOULD, MUST NOT, MUST  * The results Filter BitSet is constructed as follows:  * SHOULD Filters are OR'd together  * The resulting Filter is NOT'd with the NOT Filters  * The resulting Filter is AND'd with the MUST Filters  * @author BPDThebault  */
end_comment
begin_class
DECL|class|BooleanFilter
specifier|public
class|class
name|BooleanFilter
extends|extends
name|Filter
block|{
comment|//ArrayList of SHOULD filters
DECL|field|shouldFilters
name|ArrayList
name|shouldFilters
init|=
literal|null
decl_stmt|;
comment|//ArrayList of NOT filters
DECL|field|notFilters
name|ArrayList
name|notFilters
init|=
literal|null
decl_stmt|;
comment|//ArrayList of MUST filters
DECL|field|mustFilters
name|ArrayList
name|mustFilters
init|=
literal|null
decl_stmt|;
comment|/** 	 * Returns the a BitSet representing the Boolean composition 	 * of the filters that have been added. 	 */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
comment|//create a new bitSet
name|BitSet
name|returnBits
init|=
literal|null
decl_stmt|;
comment|//SHOULD filters
if|if
condition|(
name|shouldFilters
operator|!=
literal|null
condition|)
block|{
name|returnBits
operator|=
operator|(
operator|(
name|Filter
operator|)
name|shouldFilters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|//			avoid changing the original bitset - it may be cached
name|returnBits
operator|=
operator|(
name|BitSet
operator|)
name|returnBits
operator|.
name|clone
argument_list|()
expr_stmt|;
if|if
condition|(
name|shouldFilters
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|shouldFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|returnBits
operator|.
name|or
argument_list|(
operator|(
operator|(
name|Filter
operator|)
name|shouldFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//NOT filters
if|if
condition|(
name|notFilters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|notFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BitSet
name|notBits
init|=
operator|(
operator|(
name|Filter
operator|)
name|notFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnBits
operator|==
literal|null
condition|)
block|{
name|returnBits
operator|=
operator|(
name|BitSet
operator|)
name|notBits
operator|.
name|clone
argument_list|()
expr_stmt|;
name|returnBits
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|returnBits
operator|.
name|andNot
argument_list|(
name|notBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//MUST filters
if|if
condition|(
name|mustFilters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mustFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BitSet
name|mustBits
init|=
operator|(
operator|(
name|Filter
operator|)
name|mustFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|returnBits
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mustFilters
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|returnBits
operator|=
name|mustBits
expr_stmt|;
block|}
else|else
block|{
comment|//don't mangle the bitset
name|returnBits
operator|=
operator|(
name|BitSet
operator|)
name|mustBits
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|returnBits
operator|.
name|and
argument_list|(
name|mustBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|returnBits
operator|==
literal|null
condition|)
block|{
name|returnBits
operator|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|returnBits
return|;
block|}
comment|/** 	 * Adds a new FilterClause to the Boolean Filter container 	 * @param filterClause A FilterClause object containing a Filter and an Occur parameter 	 */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FilterClause
name|filterClause
parameter_list|)
block|{
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
condition|)
block|{
if|if
condition|(
name|mustFilters
operator|==
literal|null
condition|)
block|{
name|mustFilters
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|mustFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
condition|)
block|{
if|if
condition|(
name|shouldFilters
operator|==
literal|null
condition|)
block|{
name|shouldFilters
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|shouldFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterClause
operator|.
name|getOccur
argument_list|()
operator|.
name|equals
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
condition|)
block|{
if|if
condition|(
name|notFilters
operator|==
literal|null
condition|)
block|{
name|notFilters
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
name|notFilters
operator|.
name|add
argument_list|(
name|filterClause
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanFilter
name|test
init|=
operator|(
name|BooleanFilter
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|notFilters
operator|==
name|test
operator|.
name|notFilters
operator|||
operator|(
name|notFilters
operator|!=
literal|null
operator|&&
name|notFilters
operator|.
name|equals
argument_list|(
name|test
operator|.
name|notFilters
argument_list|)
operator|)
operator|)
operator|&&
operator|(
name|mustFilters
operator|==
name|test
operator|.
name|mustFilters
operator|||
operator|(
name|mustFilters
operator|!=
literal|null
operator|&&
name|mustFilters
operator|.
name|equals
argument_list|(
name|test
operator|.
name|mustFilters
argument_list|)
operator|)
operator|)
operator|&&
operator|(
name|shouldFilters
operator|==
name|test
operator|.
name|shouldFilters
operator|||
operator|(
name|shouldFilters
operator|!=
literal|null
operator|&&
name|shouldFilters
operator|.
name|equals
argument_list|(
name|test
operator|.
name|shouldFilters
argument_list|)
operator|)
operator|)
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|7
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|mustFilters
condition|?
literal|0
else|:
name|mustFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|notFilters
condition|?
literal|0
else|:
name|notFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|shouldFilters
condition|?
literal|0
else|:
name|shouldFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|hash
return|;
block|}
block|}
end_class
end_unit
