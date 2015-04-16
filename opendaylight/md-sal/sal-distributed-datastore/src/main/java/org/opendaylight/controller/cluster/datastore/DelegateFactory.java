/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.cluster.datastore;

import java.util.Map.Entry;

/**
 * Base class for factories instantiating delegates.
 *
 * <D> delegate type
 * <M> message type
 * <I> initial state type
 */
abstract class DelegateFactory<M, D, I> {
    abstract Entry<D, I> createDelegate(M message);
}
