begin_unit
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
comment|/**  * contains a HashMap of all URLs already passed. Adds each URL to that list, or  * consumes it if it is already present  *  * @todo find ways to reduce memory consumption here. the approach is somewhat naive  *  * @author    Clemens Marschner  * @created   3. Januar 2002  * @version $Id$  */
end_comment
begin_class
DECL|class|URLVisitedFilter
specifier|public
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
block|{     }
comment|//SimpleLogger log;
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
name|SimpleLogger
name|log
parameter_list|,
name|int
name|initialHashCapacity
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
comment|//urlVector = new Vector(initialHashCapacity);
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
DECL|field|log
name|SimpleLogger
name|log
decl_stmt|;
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
comment|/**q      * @param message  Description of the Parameter      * @return         Description of the Return Value      */
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
name|getNormalizedURLString
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
comment|//log.log("URLVisitedFilter: " + urlString + " already present.");
name|log
operator|.
name|log
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|filtered
operator|++
expr_stmt|;
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
