mappingrpc
==========

TODO
===========
version 1.x
-----------------

-DmappingRpcDebug=true or log.isDebugEnable to open loggingHandler

merge biz threadpool to EventLoopGroup 

Done
===========
MappingPackageClient serverList support multiple server

MappingPackageClient reconnect

Function
===========
callOnlineUser
listOnlineUser
pushToRequestUser
pushToOnlineUser

Feature
===========
target:two way rpc
easy session api:multiple session model
self session:multi api level
seperate not important:process flow with Thread Model
spi(Service Program Interface) with client fixture

Session
===========
client session:cross connection, same client instance
login session: cross client instance
device session: seperate device session

Api Thread Model
===========
ReturnAndContinue/MultiStep:not only request/resply，is a response set
not SEDA

Api Level
===========
SNA + login self management
login tag

Concept
===========
throughThreadContext
throughAsyncToken
connectionToken
sessionToken

bizCallFromClient
bizCallFromServer
bizReturnToClient
serverTagToConnection
serverTagToServerSession
serverTagToClientSession/clientTagToClientSession

loginTagOnClientSession
loginTagWithDeviceSession


