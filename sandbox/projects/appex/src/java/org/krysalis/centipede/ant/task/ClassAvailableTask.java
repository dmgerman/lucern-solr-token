begin_unit
begin_comment
comment|/*****************************************************************************  * Copyright (C) The Apache Software Foundation. All rights reserved.        *  * ------------------------------------------------------------------------- *  * This software is published under the terms of the Apache Software License *  * version 1.1, a copy of which has been included  with this distribution in *  * the LICENSE file.                                                         *  *****************************************************************************/
end_comment
begin_package
DECL|package|org.krysalis.centipede.ant.task
package|package
name|org
operator|.
name|krysalis
operator|.
name|centipede
operator|.
name|ant
operator|.
name|task
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|taskdefs
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|types
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Will set the given property if the requested class is available in the  * specified classpath. The found class is not loaded!  * This class is heavily based on the available task in the ant package:  * @author Stefano Mazzocchi<a href="mailto:stefano@apache.org">stefano@apache.org</a>  *  * This task searches only in the defined path but not in the parents path  * unless explicitly overridden by the value of ${build.sysclasspath}  * like the original available task does.  * @author<a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>  * @version CVS $Revision$ $Date$  */
end_comment
begin_class
DECL|class|ClassAvailableTask
specifier|public
class|class
name|ClassAvailableTask
extends|extends
name|Task
block|{
comment|/**      * A hashtable of zip files opened by the classloader      */
DECL|field|zipFiles
specifier|private
name|Hashtable
name|zipFiles
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|property
specifier|private
name|String
name|property
decl_stmt|;
DECL|field|classname
specifier|private
name|String
name|classname
decl_stmt|;
DECL|field|classpath
specifier|private
name|Path
name|classpath
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
init|=
literal|"true"
decl_stmt|;
DECL|method|setClasspath
specifier|public
name|void
name|setClasspath
parameter_list|(
name|Path
name|classpath
parameter_list|)
block|{
name|createClasspath
argument_list|()
operator|.
name|append
argument_list|(
name|classpath
argument_list|)
expr_stmt|;
block|}
DECL|method|createClasspath
specifier|public
name|Path
name|createClasspath
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|classpath
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|classpath
operator|=
operator|new
name|Path
argument_list|(
name|this
operator|.
name|project
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|classpath
operator|.
name|createPath
argument_list|()
return|;
block|}
DECL|method|setClasspathRef
specifier|public
name|void
name|setClasspathRef
parameter_list|(
name|Reference
name|r
parameter_list|)
block|{
name|createClasspath
argument_list|()
operator|.
name|setRefid
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
DECL|method|setProperty
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|)
block|{
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setClassname
specifier|public
name|void
name|setClassname
parameter_list|(
name|String
name|classname
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|classname
argument_list|)
condition|)
block|{
name|this
operator|.
name|classname
operator|=
name|classname
expr_stmt|;
block|}
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"property attribute is required"
argument_list|,
name|location
argument_list|)
throw|;
block|}
if|if
condition|(
name|eval
argument_list|()
condition|)
block|{
name|this
operator|.
name|project
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|eval
specifier|public
name|boolean
name|eval
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|classname
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"At least one of (classname|file|resource) is required"
argument_list|,
name|location
argument_list|)
throw|;
block|}
if|if
condition|(
name|classpath
operator|!=
literal|null
condition|)
block|{
name|classpath
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|classpath
operator|=
name|classpath
operator|.
name|concatSystemClasspath
argument_list|(
literal|"ignore"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|findClassInComponents
argument_list|(
name|classname
argument_list|)
condition|)
block|{
name|log
argument_list|(
literal|"Unable to load class "
operator|+
name|classname
operator|+
literal|" to set property "
operator|+
name|property
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Get an inputstream to a given resource in the given file which may      * either be a directory or a zip file.      *      * @param file the file (directory or jar) in which to search for the resource.      * @param resourceName the name of the resource for which a stream is required.      *      * @return a stream to the required resource or null if the resource cannot be      * found in the given file object      */
DECL|method|contains
specifier|private
name|boolean
name|contains
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
name|resource
init|=
operator|new
name|File
argument_list|(
name|file
argument_list|,
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
comment|// is the zip file in the cache
name|ZipFile
name|zipFile
init|=
operator|(
name|ZipFile
operator|)
name|zipFiles
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|zipFile
operator|==
literal|null
condition|)
block|{
name|zipFile
operator|=
operator|new
name|ZipFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|zipFiles
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|zipFile
argument_list|)
expr_stmt|;
block|}
name|ZipEntry
name|entry
init|=
name|zipFile
operator|.
name|getEntry
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"Ignoring Exception "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" reading resource "
operator|+
name|resourceName
operator|+
literal|" from "
operator|+
name|file
argument_list|,
name|Project
operator|.
name|MSG_VERBOSE
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Find a class on the given classpath.      */
DECL|method|findClassInComponents
specifier|private
name|boolean
name|findClassInComponents
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// we need to search the components of the path to see if we can find the
comment|// class we want.
specifier|final
name|String
name|classname
init|=
name|name
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".class"
decl_stmt|;
specifier|final
name|String
index|[]
name|list
init|=
name|classpath
operator|.
name|list
argument_list|()
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|list
operator|.
name|length
operator|&&
name|found
operator|==
literal|false
condition|)
block|{
specifier|final
name|File
name|pathComponent
init|=
operator|(
name|File
operator|)
name|project
operator|.
name|resolveFile
argument_list|(
name|list
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|found
operator|=
name|this
operator|.
name|contains
argument_list|(
name|pathComponent
argument_list|,
name|classname
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
name|found
return|;
block|}
block|}
end_class
end_unit
