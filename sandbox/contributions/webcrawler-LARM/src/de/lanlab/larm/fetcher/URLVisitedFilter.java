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
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|SimpleLogger
import|;
end_import
begin_comment
comment|/**  * contains a HashMap of all URLs already passed. Adds each URL to that list, or  * consumes it if it is already present  *  * @todo find ways to reduce memory consumption here. the approach is somewhat naive  *  * @author    Clemens Marschner  * @created   3. Januar 2002  */
end_comment
begin_class
DECL|class|URLVisitedFilter
class|class
name|URLVisitedFilter
extends|extends
name|Filter
implements|implements
name|MessageListener
block|{
comment|/**      * Description of the Method      *      * @param handler  Description of the Parameter      */
DECL|method|notifyAddedToMessageHandler
specifier|public
name|void
name|notifyAddedToMessageHandler
parameter_list|(
name|MessageHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|messageHandler
operator|=
name|handler
expr_stmt|;
block|}
DECL|field|messageHandler
name|MessageHandler
name|messageHandler
decl_stmt|;
DECL|field|log
name|SimpleLogger
name|log
decl_stmt|;
DECL|field|urlHash
name|HashSet
name|urlHash
decl_stmt|;
DECL|field|dummy
specifier|static
name|Boolean
name|dummy
init|=
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/**      * Constructor for the URLVisitedFilter object      *      * @param initialHashCapacity  Description of the Parameter      */
DECL|method|URLVisitedFilter
specifier|public
name|URLVisitedFilter
parameter_list|(
name|int
name|initialHashCapacity
parameter_list|,
name|SimpleLogger
name|log
parameter_list|)
block|{
name|urlHash
operator|=
operator|new
name|HashSet
argument_list|(
name|initialHashCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
comment|//urlVector = new Vector(initialHashCapacity);
block|}
comment|/**      * clears everything      */
DECL|method|clearHashtable
specifier|public
name|void
name|clearHashtable
parameter_list|()
block|{
name|urlHash
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// urlVector.clear();
block|}
comment|/**      * @param message  Description of the Parameter      * @return         Description of the Return Value      */
DECL|method|handleRequest
specifier|public
name|Message
name|handleRequest
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
if|if
condition|(
name|message
operator|instanceof
name|URLMessage
condition|)
block|{
name|URLMessage
name|urlMessage
init|=
operator|(
operator|(
name|URLMessage
operator|)
name|message
operator|)
decl_stmt|;
name|URL
name|url
init|=
name|urlMessage
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|String
name|urlString
init|=
name|urlMessage
operator|.
name|getURLString
argument_list|()
decl_stmt|;
if|if
condition|(
name|urlHash
operator|.
name|contains
argument_list|(
name|urlString
argument_list|)
condition|)
block|{
comment|//System.out.println("URLVisitedFilter: " + urlString + " already present.");
name|filtered
operator|++
expr_stmt|;
if|if
condition|(
name|log
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|logThreadSafe
argument_list|(
name|urlMessage
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// System.out.println("URLVisitedFilter: " + urlString + " not present yet.");
name|urlHash
operator|.
name|add
argument_list|(
name|urlString
argument_list|)
expr_stmt|;
name|stringSize
operator|+=
name|urlString
operator|.
name|length
argument_list|()
expr_stmt|;
comment|// see below
comment|//urlVector.add(urlString);
block|}
block|}
return|return
name|message
return|;
block|}
DECL|field|stringSize
specifier|private
name|int
name|stringSize
init|=
literal|0
decl_stmt|;
comment|/**      * just a method to get a rough number of characters contained in the array      * with that you see that the total memory  is mostly used by this class      */
DECL|method|getStringSize
specifier|public
name|int
name|getStringSize
parameter_list|()
block|{
return|return
name|stringSize
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|urlHash
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class
end_unit
