begin_unit
begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package
begin_comment
comment|/**  * Title: LARM Lanlab Retrieval Machine Description: Copyright: Copyright (c)  * Company:  *  * @author  * @version   1.0  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_comment
comment|/**  * Description of the Class  *  * @author    Administrator  * @created   16. Februar 2002  */
end_comment
begin_class
DECL|class|HostManager
specifier|public
class|class
name|HostManager
block|{
DECL|field|hosts
name|HashMap
name|hosts
decl_stmt|;
DECL|field|hostCount
specifier|static
name|int
name|hostCount
init|=
literal|0
decl_stmt|;
comment|/**      * Constructor for the HostInfo object      *      * @param initialSize  Description of the Parameter      */
DECL|method|HostManager
specifier|public
name|HostManager
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|hosts
operator|=
operator|new
name|HashMap
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
comment|/**      * Description of the Method      *      * @param hostName  Description of the Parameter      * @return          Description of the Return Value      */
DECL|method|put
specifier|public
name|HostInfo
name|put
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|hosts
operator|.
name|containsKey
argument_list|(
name|hostName
argument_list|)
condition|)
block|{
name|int
name|hostID
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|hostID
operator|=
name|hostCount
operator|++
expr_stmt|;
block|}
name|HostInfo
name|hi
init|=
operator|new
name|HostInfo
argument_list|(
name|hostName
argument_list|,
name|hostID
argument_list|)
decl_stmt|;
name|hosts
operator|.
name|put
argument_list|(
name|hostName
argument_list|,
name|hi
argument_list|)
expr_stmt|;
return|return
name|hi
return|;
block|}
return|return
operator|(
name|HostInfo
operator|)
name|hosts
operator|.
name|get
argument_list|(
name|hostName
argument_list|)
return|;
comment|/*else         {             hostID = hosts.get()         }         // assert hostID != -1;         return hostID;*/
block|}
comment|/**      * Gets the hostID attribute of the HostInfo object      *      * @param hostName  Description of the Parameter      * @return          The hostID value      */
DECL|method|getHostInfo
specifier|public
name|HostInfo
name|getHostInfo
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
name|HostInfo
name|hi
init|=
operator|(
name|HostInfo
operator|)
name|hosts
operator|.
name|get
argument_list|(
name|hostName
argument_list|)
decl_stmt|;
if|if
condition|(
name|hi
operator|==
literal|null
condition|)
block|{
return|return
name|put
argument_list|(
name|hostName
argument_list|)
return|;
block|}
return|return
name|hi
return|;
block|}
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|hosts
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class
end_unit
