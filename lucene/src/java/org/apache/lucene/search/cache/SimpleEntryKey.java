begin_unit
begin_package
DECL|package|org.apache.lucene.search.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|cache
package|;
end_package
begin_class
DECL|class|SimpleEntryKey
specifier|public
class|class
name|SimpleEntryKey
extends|extends
name|EntryKey
block|{
DECL|field|clazz
specifier|public
specifier|final
name|Class
name|clazz
decl_stmt|;
DECL|field|args
specifier|public
specifier|final
name|Object
index|[]
name|args
decl_stmt|;
DECL|field|hash
specifier|public
specifier|final
name|int
name|hash
decl_stmt|;
DECL|method|SimpleEntryKey
specifier|public
name|SimpleEntryKey
parameter_list|(
name|Class
name|clazz
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|int
name|hash
init|=
name|clazz
operator|.
name|hashCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|obj
range|:
name|args
control|)
block|{
name|hash
operator|^=
name|obj
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
block|}
annotation|@
name|Override
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
name|obj
operator|instanceof
name|SimpleEntryKey
condition|)
block|{
name|SimpleEntryKey
name|key
init|=
operator|(
name|SimpleEntryKey
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|hash
operator|!=
name|hash
operator|||
name|key
operator|.
name|clazz
operator|!=
name|clazz
operator|||
name|key
operator|.
name|args
operator|.
name|length
operator|!=
name|args
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// In the off chance that the hash etc is all the same
comment|// we should actually check the values
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|key
operator|.
name|args
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
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
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|args
control|)
block|{
name|str
operator|.
name|append
argument_list|(
name|v
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
name|hash
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
