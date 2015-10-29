begin_unit
comment|'# Licensed to the Apache Software Foundation (ASF) under one or more'
nl|'\n'
comment|'# contributor license agreements.  See the NOTICE file distributed with'
nl|'\n'
comment|'# this work for additional information regarding copyright ownership.'
nl|'\n'
comment|'# The ASF licenses this file to You under the Apache License, Version 2.0'
nl|'\n'
comment|'# (the "License"); you may not use this file except in compliance with'
nl|'\n'
comment|'# the License.  You may obtain a copy of the License at'
nl|'\n'
comment|'#'
nl|'\n'
comment|'#     http://www.apache.org/licenses/LICENSE-2.0'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# Unless required by applicable law or agreed to in writing, software'
nl|'\n'
comment|'# distributed under the License is distributed on an "AS IS" BASIS,'
nl|'\n'
comment|'# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.'
nl|'\n'
comment|'# See the License for the specific language governing permissions and'
nl|'\n'
comment|'# limitations under the License.'
nl|'\n'
nl|'\n'
string|'""" Workaround for slow updates from svn to git.\n\nSituation:\n\nRemove svn repo   ---> slow git-svn update process --->    Remote git-svn repo (upstream)\n   |                                                        |\n   |                                                        |\n   v                                                        v\nLocal svn working copy --> this workaround         --->    Local git repo\n\nBecause of the slow remote git-svn update process the remote git repo is (far) behind\nthe remote svn repo.\n\n\nFor a branch branchname in a local git repository following an upstream git-svn git repository,\nthis maintains commits on a temporary git branch branchname.svn in the local git repository.\nThese commits contain a message ending like this:\n  "RepoUrl patch of svn diff -r EarlierSvnRevisionNumber:LatestSvnRevisionNumber".\n\nThe earlier revision number is taken from the git-svn-id message of git svn,\nor from the LatestSvnRevisionNumber in the commit message of branchname.svn,\nwhichever is later.\n\nThis allows branchname.svn to be used as a local git branch instead of branchname\nto develop new features locally, usually by mering branchname.svn into a feature branch.\nOnce the normal git-svn branch is up to date, it can also be merged.\n\nIn more detail:\n  - update the svn working copy of the branch to the latest revision,\n  - in the git repo:\n  - fetch the git repository from upstream.\n  - merge branchname from upstream/branchname, this is the branch that can be (far) behind.\n  - use the git-svn-id from the latest git commit on this branch to determine the corresponding svn revision.\n  - if the branchname.svn exists determine the latest svn revision from there.\n  - choose the latest svn revision number available.\n  - compare the git-svn revision to the svn latest revision (delay deleting a too early branchname.svn to later below).\n  - when the git repository has the same revision:\n    - exit reporting that branchname is up to date.\n  - when the git repository has an earlier revision:\n    - in the svn working copy, create a patch from the earlier revision into file ~/patches/branchname.svn\n    - in the git working tree:\n      - if branchname.svn is not at the earlier svn revision number, delete branchname.svn\n      - if necessary create branch branchname.svn from branchname.\n      - check out branchname.svn\n      - apply the patch ~/patches/branchname.svn, ignoring whitespace differences.\n      - commit with a message with revision numbers as indicated above\n"""'
newline|'\n'
nl|'\n'
name|'import'
name|'os'
newline|'\n'
name|'import'
name|'subprocess'
newline|'\n'
name|'import'
name|'StringIO'
newline|'\n'
nl|'\n'
nl|'\n'
DECL|function|svnSeq
name|'def'
name|'svnSeq'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
op|'('
string|'"svn"'
op|','
op|')'
newline|'\n'
nl|'\n'
DECL|function|callSvn
dedent|''
name|'def'
name|'callSvn'
op|'('
op|'*'
name|'args'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'subprocess'
op|'.'
name|'check_call'
op|'('
name|'svnSeq'
op|'('
op|')'
op|'+'
name|'args'
op|')'
newline|'\n'
nl|'\n'
DECL|function|callSvnStdout
dedent|''
name|'def'
name|'callSvnStdout'
op|'('
op|'*'
name|'args'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'subprocess'
op|'.'
name|'check_output'
op|'('
name|'svnSeq'
op|'('
op|')'
op|'+'
name|'args'
op|')'
newline|'\n'
nl|'\n'
DECL|function|callSvnStdoutToFile
dedent|''
name|'def'
name|'callSvnStdoutToFile'
op|'('
name|'f'
op|','
op|'*'
name|'args'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'subprocess'
op|'.'
name|'check_call'
op|'('
name|'svnSeq'
op|'('
op|')'
op|'+'
name|'args'
op|','
name|'stdout'
op|'='
name|'f'
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
DECL|function|gitCommand
dedent|''
name|'def'
name|'gitCommand'
op|'('
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
string|'"git"'
newline|'\n'
nl|'\n'
DECL|function|gitAndRepoList
dedent|''
name|'def'
name|'gitAndRepoList'
op|'('
name|'gitRepo'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
op|'('
name|'gitCommand'
op|'('
op|')'
op|','
string|'"-C"'
op|','
name|'gitRepo'
op|')'
newline|'\n'
nl|'\n'
DECL|function|callGitRepo
dedent|''
name|'def'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
op|'*'
name|'args'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'subprocess'
op|'.'
name|'check_call'
op|'('
name|'gitAndRepoList'
op|'('
name|'gitRepo'
op|')'
op|'+'
name|'args'
op|')'
newline|'\n'
nl|'\n'
DECL|function|callGitStdout
dedent|''
name|'def'
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
op|'*'
name|'args'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'subprocess'
op|'.'
name|'check_output'
op|'('
name|'gitAndRepoList'
op|'('
name|'gitRepo'
op|')'
op|'+'
name|'args'
op|')'
newline|'\n'
nl|'\n'
DECL|function|getGitCommitMessage
dedent|''
name|'def'
name|'getGitCommitMessage'
op|'('
name|'gitRepo'
op|','
name|'commitRef'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
string|'"log"'
op|','
string|'"--format=%B"'
op|','
string|'"-n"'
op|','
string|'"1"'
op|','
name|'commitRef'
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
nl|'\n'
DECL|function|lastChangedSvnRevision
dedent|''
name|'def'
name|'lastChangedSvnRevision'
op|'('
name|'svnInfo'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'lastChangedMarker'
op|'='
string|'"Last Changed Rev: "'
newline|'\n'
name|'after'
op|'='
name|'svnInfo'
op|'.'
name|'split'
op|'('
name|'lastChangedMarker'
op|')'
op|'['
number|'1'
op|']'
newline|'\n'
name|'splitAfter'
op|'='
name|'after'
op|'.'
name|'split'
op|'('
op|')'
newline|'\n'
name|'return'
name|'int'
op|'('
name|'splitAfter'
op|'['
number|'0'
op|']'
op|')'
newline|'\n'
nl|'\n'
nl|'\n'
nl|'\n'
DECL|function|getGitSvnRemoteAndRevision
dedent|''
name|'def'
name|'getGitSvnRemoteAndRevision'
op|'('
name|'gitSvnCommitMessage'
op|')'
op|':'
comment|'# from a git-svn commit'
newline|'\n'
indent|'  '
name|'words'
op|'='
name|'gitSvnCommitMessage'
op|'.'
name|'split'
op|'('
op|')'
op|';'
newline|'\n'
name|'svnIdMarker'
op|'='
string|'"git-svn-id:"'
newline|'\n'
name|'assert'
name|'words'
op|'.'
name|'index'
op|'('
name|'svnIdMarker'
op|')'
op|'>='
number|'0'
newline|'\n'
name|'svnId'
op|'='
name|'words'
op|'['
name|'words'
op|'.'
name|'index'
op|'('
name|'svnIdMarker'
op|')'
op|'+'
number|'1'
op|']'
newline|'\n'
name|'splitSvnId'
op|'='
name|'svnId'
op|'.'
name|'split'
op|'('
string|'"@"'
op|')'
newline|'\n'
name|'return'
op|'('
name|'splitSvnId'
op|'['
number|'0'
op|']'
op|','
name|'int'
op|'('
name|'splitSvnId'
op|'['
number|'1'
op|']'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|lastTempGitSvnRevision
dedent|''
name|'def'
name|'lastTempGitSvnRevision'
op|'('
name|'gitCommitMessage'
op|')'
op|':'
comment|'# from a commit generated here on the temp branch.'
newline|'\n'
indent|'  '
name|'parts'
op|'='
name|'gitCommitMessage'
op|'.'
name|'split'
op|'('
string|'":"'
op|')'
newline|'\n'
name|'lastPart'
op|'='
name|'parts'
op|'['
op|'-'
number|'1'
op|']'
op|'.'
name|'split'
op|'('
op|')'
op|'['
number|'0'
op|']'
comment|'# remove appended newlines'
newline|'\n'
name|'try'
op|':'
newline|'\n'
indent|'    '
name|'return'
name|'int'
op|'('
name|'lastPart'
op|')'
newline|'\n'
dedent|''
name|'except'
op|':'
comment|'# not generated here, ignore.'
newline|'\n'
indent|'    '
name|'print'
string|'"Warning: svn revision range not found at end of commit message:\\n"'
op|','
name|'gitCommitMessage'
newline|'\n'
name|'return'
name|'None'
newline|'\n'
nl|'\n'
nl|'\n'
DECL|function|errorExit
dedent|''
dedent|''
name|'def'
name|'errorExit'
op|'('
op|'*'
name|'messageParts'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'raise'
name|'Exception'
op|'('
string|'" "'
op|'.'
name|'join'
op|'('
name|'messageParts'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|maintainTempGitSvnBranch
dedent|''
name|'def'
name|'maintainTempGitSvnBranch'
op|'('
name|'branchName'
op|','
name|'tempGitBranchName'
op|','
name|'svnWorkingCopyOfBranch'
op|','
name|'gitRepo'
op|','
name|'gitUpstream'
op|','
name|'patchFileName'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"checkout"'
op|','
name|'branchName'
op|')'
comment|'# fail when git working tree is not clean'
newline|'\n'
nl|'\n'
comment|'# CHECKME: add svn switch to branch here?'
nl|'\n'
nl|'\n'
name|'callSvn'
op|'('
string|'"update"'
op|','
name|'svnWorkingCopyOfBranch'
op|')'
newline|'\n'
nl|'\n'
name|'svnInfo'
op|'='
name|'callSvnStdout'
op|'('
string|'"info"'
op|','
name|'svnWorkingCopyOfBranch'
op|')'
newline|'\n'
comment|'# print "svnInfo:", svnInfo'
nl|'\n'
name|'lastSvnRevision'
op|'='
name|'lastChangedSvnRevision'
op|'('
name|'svnInfo'
op|')'
newline|'\n'
name|'print'
name|'svnWorkingCopyOfBranch'
op|','
string|'"lastSvnRevision:"'
op|','
name|'lastSvnRevision'
newline|'\n'
nl|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"fetch"'
op|','
name|'gitUpstream'
op|')'
newline|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"merge"'
op|','
name|'branchName'
op|','
name|'gitUpstream'
op|'+'
string|'"/"'
op|'+'
name|'branchName'
op|')'
newline|'\n'
name|'lastGitCommitMessage'
op|'='
name|'getGitCommitMessage'
op|'('
name|'gitRepo'
op|','
name|'branchName'
op|')'
newline|'\n'
name|'print'
string|'"lastGitCommitMessage:\\n"'
op|','
name|'lastGitCommitMessage'
newline|'\n'
op|'('
name|'svnRemote'
op|','
name|'lastSvnRevisionOnGitSvnBranch'
op|')'
op|'='
name|'getGitSvnRemoteAndRevision'
op|'('
name|'lastGitCommitMessage'
op|')'
newline|'\n'
name|'print'
string|'"svnRemote:"'
op|','
name|'svnRemote'
newline|'\n'
name|'print'
name|'gitRepo'
op|','
name|'branchName'
op|','
string|'"lastSvnRevisionOnGitSvnBranch:"'
op|','
name|'lastSvnRevisionOnGitSvnBranch'
newline|'\n'
nl|'\n'
comment|'# check whether tempGitBranchName exists:'
nl|'\n'
name|'diffBaseRevision'
op|'='
name|'lastSvnRevisionOnGitSvnBranch'
newline|'\n'
name|'svnTempRevision'
op|'='
name|'None'
newline|'\n'
name|'doCommitOnExistingTempBranch'
op|'='
name|'False'
newline|'\n'
name|'listOut'
op|'='
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
string|'"branch"'
op|','
string|'"--list"'
op|','
name|'tempGitBranchName'
op|')'
newline|'\n'
name|'if'
name|'listOut'
op|':'
comment|'# tempGitBranchName exists'
newline|'\n'
indent|'    '
name|'print'
name|'tempGitBranchName'
op|','
string|'"exists"'
newline|'\n'
name|'lastGitCommitMessage'
op|'='
name|'getGitCommitMessage'
op|'('
name|'gitRepo'
op|','
name|'tempGitBranchName'
op|')'
newline|'\n'
comment|'# update lastSvnRevisionOnGitSvnBranch from there.'
nl|'\n'
name|'svnTempRevision'
op|'='
name|'lastTempGitSvnRevision'
op|'('
name|'lastGitCommitMessage'
op|')'
newline|'\n'
name|'if'
name|'svnTempRevision'
name|'is'
name|'not'
name|'None'
op|':'
newline|'\n'
indent|'      '
name|'if'
name|'svnTempRevision'
op|'>'
name|'lastSvnRevisionOnGitSvnBranch'
op|':'
newline|'\n'
indent|'        '
name|'doCommitOnExistingTempBranch'
op|'='
name|'True'
newline|'\n'
name|'diffBaseRevision'
op|'='
name|'svnTempRevision'
newline|'\n'
nl|'\n'
dedent|''
dedent|''
dedent|''
name|'if'
name|'doCommitOnExistingTempBranch'
op|':'
newline|'\n'
indent|'    '
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"checkout"'
op|','
name|'tempGitBranchName'
op|')'
comment|'# checkout the temp branch.'
newline|'\n'
name|'currentGitBranch'
op|'='
name|'tempGitBranchName'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'currentGitBranch'
op|'='
name|'branchName'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'lastSvnRevision'
op|'=='
name|'diffBaseRevision'
op|':'
newline|'\n'
indent|'    '
name|'print'
name|'gitRepo'
op|','
name|'currentGitBranch'
op|','
string|'"up to date with"'
op|','
name|'svnWorkingCopyOfBranch'
newline|'\n'
name|'return'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'lastSvnRevision'
op|'<'
name|'diffBaseRevision'
op|':'
comment|'# unlikely, do nothing'
newline|'\n'
indent|'    '
name|'print'
name|'gitRepo'
op|','
name|'currentGitBranch'
op|','
string|'"later than"'
op|','
name|'svnWorkingCopyOfBranch'
op|','
string|'", nothing to update."'
newline|'\n'
name|'return'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
name|'gitRepo'
op|','
name|'currentGitBranch'
op|','
string|'"earlier than"'
op|','
name|'svnWorkingCopyOfBranch'
newline|'\n'
nl|'\n'
comment|'# assert that the git working tree is on branchName'
nl|'\n'
name|'gitStatus'
op|'='
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
string|'"status"'
op|')'
newline|'\n'
comment|'# print "gitStatus:\\n", gitStatus'
nl|'\n'
nl|'\n'
name|'statusParts'
op|'='
name|'gitStatus'
op|'.'
name|'split'
op|'('
string|'"On branch"'
op|')'
newline|'\n'
name|'actualBranchName'
op|'='
name|'statusParts'
op|'['
number|'1'
op|']'
op|'.'
name|'split'
op|'('
op|')'
op|'['
number|'0'
op|']'
newline|'\n'
name|'if'
name|'actualBranchName'
op|'!='
name|'currentGitBranch'
op|':'
newline|'\n'
indent|'    '
name|'errorExit'
op|'('
name|'gitRepo'
op|','
string|'"on unexpected branch"'
op|','
name|'actualBranchName'
op|','
string|'"but expected"'
op|','
name|'currentGitBranch'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'expSubString'
op|'='
string|'"nothing to commit, working directory clean"'
newline|'\n'
name|'if'
name|'gitStatus'
op|'.'
name|'find'
op|'('
name|'expSubString'
op|')'
op|'<'
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'errorExit'
op|'('
name|'gitRepo'
op|','
string|'"on branch"'
op|','
name|'actualBranchName'
op|','
string|'"not clean"'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'print'
name|'gitRepo'
op|','
string|'"on branch"'
op|','
name|'actualBranchName'
op|','
string|'"and clean"'
newline|'\n'
nl|'\n'
comment|'# create patch file from svn between the revisions:'
nl|'\n'
name|'revisionsRange'
op|'='
name|'str'
op|'('
name|'diffBaseRevision'
op|')'
op|'+'
string|'":"'
op|'+'
name|'str'
op|'('
name|'lastSvnRevision'
op|')'
newline|'\n'
name|'patchFile'
op|'='
name|'open'
op|'('
name|'patchFileName'
op|','
string|"'w'"
op|')'
newline|'\n'
name|'print'
string|'"Creating patch from"'
op|','
name|'svnWorkingCopyOfBranch'
op|','
string|'"between revisions"'
op|','
name|'revisionsRange'
newline|'\n'
name|'callSvnStdoutToFile'
op|'('
name|'patchFile'
op|','
nl|'\n'
string|'"diff"'
op|','
string|'"-r"'
op|','
name|'revisionsRange'
op|','
nl|'\n'
name|'svnWorkingCopyOfBranch'
op|')'
newline|'\n'
name|'patchFile'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'print'
string|'"Created patch"'
op|','
name|'patchFileName'
newline|'\n'
nl|'\n'
name|'if'
name|'not'
name|'doCommitOnExistingTempBranch'
op|':'
newline|'\n'
indent|'    '
name|'listOut'
op|'='
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
string|'"branch"'
op|','
string|'"--list"'
op|','
name|'tempGitBranchName'
op|')'
newline|'\n'
name|'if'
name|'listOut'
op|':'
comment|'# tempGitBranchName exists, delete it first.'
newline|'\n'
indent|'      '
name|'print'
name|'tempGitBranchName'
op|','
string|'"exists, deleting"'
newline|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"branch"'
op|','
string|'"-D"'
op|','
name|'tempGitBranchName'
op|')'
newline|'\n'
comment|'# verify deletion:'
nl|'\n'
name|'listOut'
op|'='
name|'callGitStdout'
op|'('
name|'gitRepo'
op|','
string|'"branch"'
op|','
string|'"--list"'
op|','
name|'tempGitBranchName'
op|')'
newline|'\n'
name|'if'
name|'listOut'
op|':'
newline|'\n'
indent|'        '
name|'errorExit'
op|'('
string|'"Could not delete"'
op|','
name|'tempGitBranchName'
op|','
string|'"("'
op|','
name|'listOut'
op|','
string|'")"'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"branch"'
op|','
name|'tempGitBranchName'
op|')'
comment|'# create a new tempGitBranchName'
newline|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"checkout"'
op|','
name|'tempGitBranchName'
op|')'
comment|'# checkout tempGitBranchName'
newline|'\n'
nl|'\n'
comment|'# apply the patch'
nl|'\n'
dedent|''
dedent|''
name|'subprocess'
op|'.'
name|'check_call'
op|'('
op|'('
name|'gitCommand'
op|'('
op|')'
op|','
string|'"apply"'
op|','
nl|'\n'
string|'"-p6"'
op|','
comment|'# FIXME: use depth of svnRepo from root to determine the depth to strip from patch.'
nl|'\n'
string|'"--whitespace=nowarn"'
op|','
nl|'\n'
op|'('
string|'"--directory="'
op|'+'
name|'gitRepo'
op|')'
op|','
nl|'\n'
name|'patchFileName'
op|')'
op|')'
newline|'\n'
nl|'\n'
comment|'# add all patch changes to the git index to be committed.'
nl|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"add"'
op|','
string|'"-A"'
op|')'
newline|'\n'
nl|'\n'
comment|'# Applying the patch leaves files that have been actually deleted at zero size.'
nl|'\n'
comment|'# Therefore delete empty patched files from the git repo that do not exist in svn working copy:'
nl|'\n'
name|'indexPrefix'
op|'='
string|'"^Index: "'
newline|'\n'
name|'patchedFileNames'
op|'='
name|'subprocess'
op|'.'
name|'check_output'
op|'('
op|'('
string|'"grep"'
op|','
name|'indexPrefix'
op|','
name|'patchFileName'
op|')'
op|')'
newline|'\n'
name|'for'
name|'indexPatchFileName'
name|'in'
name|'patchedFileNames'
op|'.'
name|'split'
op|'('
string|'"\\n"'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'patchFileName'
op|'='
name|'indexPatchFileName'
op|'['
name|'len'
op|'('
name|'indexPrefix'
op|')'
op|':'
op|']'
newline|'\n'
name|'versionControlledFileName'
op|'='
name|'patchFileName'
op|'['
name|'len'
op|'('
name|'svnWorkingCopyOfBranch'
op|')'
op|':'
op|']'
newline|'\n'
comment|'# print "Patched versionControlledFileName:", versionControlledFileName'
nl|'\n'
nl|'\n'
name|'fileNameInGitRepo'
op|'='
name|'gitRepo'
op|'+'
string|'"/"'
op|'+'
name|'versionControlledFileName'
newline|'\n'
name|'if'
name|'not'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isfile'
op|'('
name|'fileNameInGitRepo'
op|')'
op|':'
comment|'# already deleted or a directory.'
newline|'\n'
indent|'      '
name|'continue'
newline|'\n'
nl|'\n'
dedent|''
name|'fileSize'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'getsize'
op|'('
name|'fileNameInGitRepo'
op|')'
newline|'\n'
name|'if'
name|'fileSize'
op|'>'
number|'0'
op|':'
newline|'\n'
comment|'# print "Non empty file patched normally:", fileNameInGitRepo'
nl|'\n'
indent|'      '
name|'continue'
newline|'\n'
nl|'\n'
comment|'# fileNameInGitRepo exists is empty'
nl|'\n'
dedent|''
name|'if'
name|'os'
op|'.'
name|'path'
op|'.'
name|'isfile'
op|'('
name|'patchFileName'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'print'
string|'"Left empty file:"'
op|','
name|'fileNameInGitRepo'
newline|'\n'
name|'continue'
newline|'\n'
nl|'\n'
dedent|''
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"rm"'
op|','
string|'"-f"'
op|','
comment|'# force, the file is not up to date'
nl|'\n'
name|'fileNameInGitRepo'
op|')'
newline|'\n'
comment|'# print "Deleted empty file", fileNameInGitRepo # not needed, git rm is verbose enough'
nl|'\n'
nl|'\n'
comment|'# commit'
nl|'\n'
dedent|''
name|'message'
op|'='
name|'svnRemote'
op|'+'
string|'" patch of svn diff -r "'
op|'+'
name|'revisionsRange'
newline|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
nl|'\n'
string|'"commit"'
op|','
nl|'\n'
string|'"-a"'
op|','
nl|'\n'
string|'"--message="'
op|'+'
name|'message'
op|')'
newline|'\n'
nl|'\n'
name|'callGitRepo'
op|'('
name|'gitRepo'
op|','
string|'"clean"'
op|','
string|'"-fd"'
op|')'
comment|'# delete untracked directories and files'
newline|'\n'
nl|'\n'
nl|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|'"__main__"'
op|':'
newline|'\n'
nl|'\n'
DECL|variable|repo
indent|'  '
name|'repo'
op|'='
string|'"lucene-solr"'
newline|'\n'
DECL|variable|branchName
name|'branchName'
op|'='
string|'"trunk"'
newline|'\n'
DECL|variable|tempGitBranchName
name|'tempGitBranchName'
op|'='
name|'branchName'
op|'+'
string|'".svn"'
newline|'\n'
nl|'\n'
DECL|variable|home
name|'home'
op|'='
name|'os'
op|'.'
name|'path'
op|'.'
name|'expanduser'
op|'('
string|'"~"'
op|')'
newline|'\n'
nl|'\n'
DECL|variable|svnWorkingCopyOfBranch
name|'svnWorkingCopyOfBranch'
op|'='
name|'home'
op|'+'
string|'"/svnwork/"'
op|'+'
name|'repo'
op|'+'
string|'"/"'
op|'+'
name|'branchName'
newline|'\n'
comment|'# CHECKME: branchName is not really needed here, svn can switch between branches.'
nl|'\n'
nl|'\n'
DECL|variable|gitRepo
name|'gitRepo'
op|'='
name|'home'
op|'+'
string|'"/gitrepos/"'
op|'+'
name|'repo'
newline|'\n'
DECL|variable|gitUpstream
name|'gitUpstream'
op|'='
string|'"upstream"'
newline|'\n'
nl|'\n'
DECL|variable|patchFileName
name|'patchFileName'
op|'='
name|'home'
op|'+'
string|'"/patches/"'
op|'+'
name|'tempGitBranchName'
newline|'\n'
nl|'\n'
nl|'\n'
name|'maintainTempGitSvnBranch'
op|'('
name|'branchName'
op|','
name|'tempGitBranchName'
op|','
name|'svnWorkingCopyOfBranch'
op|','
name|'gitRepo'
op|','
name|'gitUpstream'
op|','
name|'patchFileName'
op|')'
newline|'\n'
nl|'\n'
dedent|''
endmarker|''
end_unit