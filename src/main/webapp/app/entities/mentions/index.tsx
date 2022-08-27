import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Mentions from './mentions';
import MentionsDetail from './mentions-detail';
import MentionsUpdate from './mentions-update';
import MentionsDeleteDialog from './mentions-delete-dialog';

const MentionsRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Mentions />} />
    <Route path="new" element={<MentionsUpdate />} />
    <Route path=":id">
      <Route index element={<MentionsDetail />} />
      <Route path="edit" element={<MentionsUpdate />} />
      <Route path="delete" element={<MentionsDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MentionsRoutes;
