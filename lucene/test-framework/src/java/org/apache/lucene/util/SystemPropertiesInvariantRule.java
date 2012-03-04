begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|MultipleFailureException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import
begin_class
DECL|class|SystemPropertiesInvariantRule
specifier|public
class|class
name|SystemPropertiesInvariantRule
implements|implements
name|TestRule
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|Statement
name|apply
parameter_list|(
specifier|final
name|Statement
name|s
parameter_list|,
name|Description
name|d
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|before
init|=
name|SystemPropertiesRestoreRule
operator|.
name|cloneAsMap
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|after
init|=
name|SystemPropertiesRestoreRule
operator|.
name|cloneAsMap
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|after
operator|.
name|equals
argument_list|(
name|before
argument_list|)
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
operator|new
name|AssertionError
argument_list|(
literal|"System properties invariant violated.\n"
operator|+
name|collectErrorMessage
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Restore original properties.
name|SystemPropertiesRestoreRule
operator|.
name|restore
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|MultipleFailureException
operator|.
name|assertEmpty
argument_list|(
name|errors
argument_list|)
expr_stmt|;
block|}
specifier|private
name|StringBuilder
name|collectErrorMessage
parameter_list|(
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|before
parameter_list|,
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|after
parameter_list|)
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|newKeys
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|after
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|newKeys
operator|.
name|removeAll
argument_list|(
name|before
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|missingKeys
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|before
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|missingKeys
operator|.
name|removeAll
argument_list|(
name|after
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|differentKeyValues
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|before
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|differentKeyValues
operator|.
name|retainAll
argument_list|(
name|after
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|differentKeyValues
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
name|String
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|valueBefore
init|=
name|before
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|String
name|valueAfter
init|=
name|after
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|valueBefore
operator|==
literal|null
operator|&&
name|valueAfter
operator|==
literal|null
operator|)
operator|||
operator|(
name|valueBefore
operator|.
name|equals
argument_list|(
name|valueAfter
argument_list|)
operator|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|missingKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"Missing keys:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|missingKeys
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|before
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|newKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"New keys:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|newKeys
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|after
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|differentKeyValues
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"Different values:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|differentKeyValues
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  [old]"
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|before
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"  [new]"
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|after
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|b
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
