begin_unit
begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Title: LARM Lanlab Retrieval Machine Description: Copyright: Copyright (c)  * Company:  *  * @author  * @version   1.0  */
end_comment
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_comment
comment|/**  * Description of the Class  *  * @author    Administrator  * @created   27. Januar 2002  */
end_comment
begin_class
DECL|class|URLUtils
specifier|public
class|class
name|URLUtils
block|{
comment|/**      * does the same as URL.toExternalForm(), but leaves out the Ref part (which we would      * cut off anyway) and handles the String Buffer so that no call of expandCapacity() will      * be necessary      * only meaningful if the default URLStreamHandler is used (as is the case with http, https, or shttp)      *      * @param u  the URL to be converted      * @return   the URL as String      */
DECL|method|toExternalFormNoRef
specifier|public
specifier|static
name|String
name|toExternalFormNoRef
parameter_list|(
name|URL
name|u
parameter_list|)
block|{
name|String
name|protocol
init|=
name|u
operator|.
name|getProtocol
argument_list|()
decl_stmt|;
name|String
name|authority
init|=
name|u
operator|.
name|getAuthority
argument_list|()
decl_stmt|;
name|String
name|file
init|=
name|u
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|(
operator|(
name|protocol
operator|==
literal|null
condition|?
literal|0
else|:
name|protocol
operator|.
name|length
argument_list|()
operator|)
operator|+
operator|(
name|authority
operator|==
literal|null
condition|?
literal|0
else|:
name|authority
operator|.
name|length
argument_list|()
operator|)
operator|+
operator|(
name|file
operator|==
literal|null
condition|?
literal|1
else|:
name|file
operator|.
name|length
argument_list|()
operator|)
operator|+
literal|3
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
if|if
condition|(
name|u
operator|.
name|getAuthority
argument_list|()
operator|!=
literal|null
operator|&&
name|u
operator|.
name|getAuthority
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|u
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|u
operator|.
name|getFile
argument_list|()
operator|!=
literal|null
operator|&&
name|u
operator|.
name|getFile
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|u
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
