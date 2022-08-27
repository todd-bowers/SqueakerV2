import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Squeak from './squeak';
import SqueakDetail from './squeak-detail';
import SqueakUpdate from './squeak-update';
import SqueakDeleteDialog from './squeak-delete-dialog';

const SqueakRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Squeak />} />
    <Route path="new" element={<SqueakUpdate />} />
    <Route path=":id">
      <Route index element={<SqueakDetail />} />
      <Route path="edit" element={<SqueakUpdate />} />
      <Route path="delete" element={<SqueakDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default SqueakRoutes;
