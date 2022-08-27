import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Squeak from './squeak';
import Tag from './tag';
import Mentions from './mentions';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="squeak/*" element={<Squeak />} />
        <Route path="tag/*" element={<Tag />} />
        <Route path="mentions/*" element={<Mentions />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
