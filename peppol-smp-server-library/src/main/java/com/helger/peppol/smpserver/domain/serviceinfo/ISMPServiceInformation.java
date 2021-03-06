/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package com.helger.peppol.smpserver.domain.serviceinfo;

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.compare.IComparator;
import com.helger.commons.id.IHasID;
import com.helger.commons.state.EChange;
import com.helger.peppol.identifier.generic.doctype.IDocumentTypeIdentifier;
import com.helger.peppol.identifier.generic.process.IProcessIdentifier;
import com.helger.peppol.smpserver.domain.extension.ISMPHasExtension;
import com.helger.peppol.smpserver.domain.servicegroup.ISMPServiceGroup;

/**
 * This interface represents the main information in a service metadata, if no
 * redirect is present. It consists of a document type identifier (
 * {@link IDocumentTypeIdentifier}) and a list of processes (
 * {@link ISMPProcess}).
 *
 * @author Philip Helger
 */
public interface ISMPServiceInformation extends Serializable, ISMPHasExtension, IHasID <String>
{
  /**
   * @return The service group to which this service information belongs. Never
   *         <code>null</code>.
   */
  @Nonnull
  ISMPServiceGroup getServiceGroup ();

  /**
   * @return The ID of the service group to which this service information
   *         belongs. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getServiceGroupID ();

  /**
   * @return The document type identifier of this service information. Never
   *         <code>null</code>.
   */
  @Nonnull
  IDocumentTypeIdentifier getDocumentTypeIdentifier ();

  /**
   * @return The number of contained process information. Always &ge; 0.
   */
  @Nonnegative
  int getProcessCount ();

  /**
   * Get the process with the specified ID
   *
   * @param aProcessID
   *        The process ID to search. May be <code>null</code>.
   * @return <code>null</code> if no such process exists
   */
  @Nullable
  ISMPProcess getProcessOfID (@Nullable IProcessIdentifier aProcessID);

  /**
   * @return A copy of the list of all processes associated with this service
   *         information.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <ISMPProcess> getAllProcesses ();

  /**
   * @return The overall endpoint count for all processes in this object. Always
   *         &ge; 0.
   */
  @Nonnegative
  int getTotalEndpointCount ();

  /**
   * Add the passed process.
   *
   * @param aProcess
   *        The process to be added. May not be <code>null</code>.
   * @throws IllegalArgumentException
   *         If a process with the same process ID is already registered.
   */
  void addProcess (@Nonnull SMPProcess aProcess);

  /**
   * Delete the provided process from this object.
   *
   * @param aProcess
   *        The process to be deleted. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if deletion was successfully,
   *         {@link EChange#UNCHANGED} otherwise.
   * @since 5.0.0
   */
  @Nonnull
  EChange deleteProcess (@Nullable ISMPProcess aProcess);

  /**
   * @return This service information object as a PEPPOL SMP JAXB object for the
   *         REST interface. Never <code>null</code>.
   */
  @Nonnull
  com.helger.peppol.smp.ServiceMetadataType getAsJAXBObjectPeppol ();

  /**
   * @return This service information object as a BDXR SMP JAXB object for the
   *         REST interface. Never <code>null</code>.
   */
  @Nonnull
  com.helger.peppol.bdxr.ServiceMetadataType getAsJAXBObjectBDXR ();

  @Nonnull
  static IComparator <ISMPServiceInformation> comparator ()
  {
    return (aElement1, aElement2) -> {
      int ret = aElement1.getServiceGroupID ().compareTo (aElement2.getServiceGroupID ());
      if (ret == 0)
        ret = aElement1.getDocumentTypeIdentifier ().compareTo (aElement2.getDocumentTypeIdentifier ());
      return ret;
    };
  }
}
