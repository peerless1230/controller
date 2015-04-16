/*
 * Copyright (c) 2015 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore;

import com.google.common.base.Preconditions;
import akka.actor.ActorRef;
import akka.actor.UntypedActorContext;
import org.opendaylight.controller.cluster.datastore.identifiers.ShardTransactionIdentifier;
import org.opendaylight.controller.cluster.datastore.jmx.mbeans.shard.ShardStats;

/**
 * A factory for creating ShardTransaction actors.
 *
 * @author Thomas Pantelis
 */
class ShardTransactionActorFactory {

    private final ShardDataTree dataTree;
    private final DatastoreContext datastoreContext;
    private final String txnDispatcherPath;
    private final ShardStats shardMBean;
    private final UntypedActorContext actorContext;
    private final ActorRef shardActor;

    ShardTransactionActorFactory(ShardDataTree dataTree, DatastoreContext datastoreContext,
            String txnDispatcherPath, ActorRef shardActor, UntypedActorContext actorContext, ShardStats shardMBean) {
        this.dataTree = Preconditions.checkNotNull(dataTree);
        this.datastoreContext = datastoreContext;
        this.txnDispatcherPath = txnDispatcherPath;
        this.shardMBean = shardMBean;
        this.actorContext = actorContext;
        this.shardActor = shardActor;
    }

    ActorRef newShardTransaction(TransactionProxy.TransactionType type, ShardTransactionIdentifier transactionID,
            String transactionChainID, short clientVersion) {
        final AbstractShardDataTreeTransaction<?> transaction;
        switch (type) {
        case READ_ONLY:
            transaction = dataTree.newReadOnlyTransaction(transactionID.toString(), transactionChainID);
            break;
        case READ_WRITE:
        case WRITE_ONLY:
            transaction = dataTree.newReadWriteTransaction(transactionID.toString(), transactionChainID);
            break;
        default:
            throw new IllegalArgumentException("Unsupported transaction type " + type);
        }

        return actorContext.actorOf(ShardTransaction.props(type, transaction, shardActor, datastoreContext, shardMBean,
                transactionID.getRemoteTransactionId(), clientVersion).withDispatcher(txnDispatcherPath),
                transactionID.toString());
    }
}
