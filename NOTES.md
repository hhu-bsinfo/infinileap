### Message Ordering

* https://github.com/openucx/ucx/issues/4490#issuecomment-557783041

  > Tag API guarantees that the message matching on the receiver will be done in order, but it does not guarantee actual message arrival order.

* https://github.com/openucx/ucx/issues/4490#issuecomment-557787152

  > You can be sure that receive buffers will be consumed in the order posted. Thus, you may use the same tag for sending small and large messages, but still be sure that messages will arrive to the correct buffers.

### Tag Matching

* https://github.com/openucx/ucx/issues/6193#issuecomment-767481955

  > please note that receive context of TAG receive is worker, so posted recv operation to worker can be matched to send form any endpoint connected to this worker according to tag and tagmask.
  
* https://github.com/openucx/ucx/issues/6370

  > If I invoke two upc_tag_send_nb on same ep one by one，Will these two send requests will be completed in the invoke order？Does it matter with whether I use RC or not?
  > 
  > *They may be completed in a different order, but will be matched in the same order on receiver*
### Streaming

* https://github.com/openucx/ucx/issues/6193#issuecomment-767481955

  > this is stream oriented API which allows to send stream of bytes on sender and receive stream of bytes on receiver, this means that point-to-point channel guarantees data ordering. For example number of send operations can be different form receive operations for the same amount of bytes, in other words you can do 1 send of 1000 bytes and 1000 receives of 1 bytes.

### Memory Registration

* https://github.com/openucx/ucx/issues/6370

  > does I need to register the buffer(secod argument of ucp_tag_send_nb ) before I invoke ucp_tag_send_nb, if not, Is ucx do it for me?
  > 
  > *No, ucx will do it internally*