begin_unit
begin_comment
comment|/*  *  LARM - LANLab Retrieval Machine  *  *  $history: $  *  */
end_comment
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
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|ThreadPool
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
name|threads
operator|.
name|ThreadPoolObserver
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
name|threads
operator|.
name|InterruptableTask
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
name|storage
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import
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
name|LinkedList
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
name|fetcher
operator|.
name|FetcherTask
import|;
end_import
begin_comment
comment|/**  * filter class; the Fetcher is the main class which keeps the ThreadPool that  * gets the documents. It should be placed at the very end of the MessageQueue,  * so that all filtering can be made beforehand.  *  * @author    Clemens Marschner  *  */
end_comment
begin_class
DECL|class|Fetcher
specifier|public
class|class
name|Fetcher
implements|implements
name|MessageListener
block|{
comment|/**      * holds the threads      */
DECL|field|fetcherPool
name|ThreadPool
name|fetcherPool
decl_stmt|;
comment|/**      * total number of docs read      */
DECL|field|docsRead
name|int
name|docsRead
init|=
literal|0
decl_stmt|;
comment|/**      * the storage where the docs are saved to      */
DECL|field|storage
name|DocumentStorage
name|storage
decl_stmt|;
comment|/**      * the host manager keeps track of host information      */
DECL|field|hostManager
name|HostManager
name|hostManager
decl_stmt|;
comment|/**      * initializes the fetcher with the given number of threads in the thread      * pool and a document storage.      *      * @param maxThreads   the number of threads in the ThreadPool      * @param storage      the storage where all documents are stored      * @param hostManager  the host manager      */
DECL|method|Fetcher
specifier|public
name|Fetcher
parameter_list|(
name|int
name|maxThreads
parameter_list|,
name|DocumentStorage
name|storage
parameter_list|,
name|HostManager
name|hostManager
parameter_list|)
block|{
name|this
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
name|FetcherTask
operator|.
name|setStorage
argument_list|(
name|storage
argument_list|)
expr_stmt|;
name|fetcherPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|maxThreads
argument_list|,
operator|new
name|FetcherThreadFactory
argument_list|(
name|hostManager
argument_list|)
argument_list|)
expr_stmt|;
name|fetcherPool
operator|.
name|setQueue
argument_list|(
operator|new
name|FetcherTaskQueue
argument_list|()
argument_list|)
expr_stmt|;
name|docsRead
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|hostManager
operator|=
name|hostManager
expr_stmt|;
block|}
comment|/**      * initializes the pool with default values (5 threads, NullStorage)      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
block|{
name|fetcherPool
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * initializes the pool with a NullStorage and the given number of threads      *      * @param maxThreads  the number of threads in the thread pool      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|int
name|maxThreads
parameter_list|)
block|{
name|fetcherPool
operator|.
name|init
argument_list|()
expr_stmt|;
name|docsRead
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * this function will be called by the message handler each time a URL      * passes all filters and gets to the fetcher. From here, it will be      * distributed to the FetcherPool, a thread pool which carries out the task,      * that is to fetch the document from the web.      *      * @param message  the message, which should actually be a URLMessage      * @return         Description of the Return Value      */
DECL|method|handleRequest
specifier|public
name|Message
name|handleRequest
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|URLMessage
name|urlMessage
init|=
operator|(
name|URLMessage
operator|)
name|message
decl_stmt|;
name|fetcherPool
operator|.
name|doTask
argument_list|(
operator|new
name|FetcherTask
argument_list|(
name|urlMessage
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|docsRead
operator|++
expr_stmt|;
comment|// eat the message
return|return
literal|null
return|;
block|}
comment|/**      * called by the message handler when this object is added to it      *      * @param handler  the message handler      */
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
name|FetcherTask
operator|.
name|setMessageHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
DECL|field|messageHandler
name|MessageHandler
name|messageHandler
decl_stmt|;
comment|/**      * the thread pool observer will be called each time a thread changes its      * state, i.e. from IDLE to RUNNING, and each time the number of thread      * queue entries change.      * this just wraps the thread pool method      *      * @param t  the class that implements the ThreadPoolObserver interface      */
DECL|method|addThreadPoolObserver
specifier|public
name|void
name|addThreadPoolObserver
parameter_list|(
name|ThreadPoolObserver
name|t
parameter_list|)
block|{
name|fetcherPool
operator|.
name|addThreadPoolObserver
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**      * returns the number of tasks queued. Should return 0 if there are any idle      * threads. this method just wraps the ThreadPool method      *      * @return   The queueSize value      */
DECL|method|getQueueSize
specifier|public
name|int
name|getQueueSize
parameter_list|()
block|{
return|return
name|fetcherPool
operator|.
name|getQueueSize
argument_list|()
return|;
block|}
comment|/**      * get the total number of threads.      * this method just wraps the ThreadPool method      *      * @return   The workingThreadsCount value      */
DECL|method|getWorkingThreadsCount
specifier|public
name|int
name|getWorkingThreadsCount
parameter_list|()
block|{
return|return
name|fetcherPool
operator|.
name|getIdleThreadsCount
argument_list|()
operator|+
name|fetcherPool
operator|.
name|getBusyThreadsCount
argument_list|()
return|;
block|}
comment|/**      * get the number of threads that are currently idle.      * this method just wraps the ThreadPool method      *      * @return   The idleThreadsCount value      */
DECL|method|getIdleThreadsCount
specifier|public
name|int
name|getIdleThreadsCount
parameter_list|()
block|{
return|return
name|fetcherPool
operator|.
name|getIdleThreadsCount
argument_list|()
return|;
block|}
comment|/**      * get the number of threads that are currently busy.      * this method just wraps the ThreadPool method      *      * @return   The busyThreadsCount value      */
DECL|method|getBusyThreadsCount
specifier|public
name|int
name|getBusyThreadsCount
parameter_list|()
block|{
return|return
name|fetcherPool
operator|.
name|getBusyThreadsCount
argument_list|()
return|;
block|}
comment|/**      * Gets the threadPool attribute of the Fetcher object      * beware: the original object is returned      *      * @TODO remove this / make it private if possible      * @return   The threadPool value      */
DECL|method|getThreadPool
specifier|public
name|ThreadPool
name|getThreadPool
parameter_list|()
block|{
return|return
name|fetcherPool
return|;
block|}
comment|/**      * Gets the total number of docs read      *      * @return   number of docs read      */
DECL|method|getDocsRead
specifier|public
name|int
name|getDocsRead
parameter_list|()
block|{
return|return
name|docsRead
return|;
block|}
comment|/**      * returns the (original) task queue      * @TODO remove this if possible      * @return   The taskQueue value      */
DECL|method|getTaskQueue
specifier|public
name|FetcherTaskQueue
name|getTaskQueue
parameter_list|()
block|{
return|return
operator|(
name|FetcherTaskQueue
operator|)
name|this
operator|.
name|fetcherPool
operator|.
name|getTaskQueue
argument_list|()
return|;
block|}
block|}
end_class
end_unit
