import squeak from 'app/entities/squeak/squeak.reducer';
import tag from 'app/entities/tag/tag.reducer';
import mentions from 'app/entities/mentions/mentions.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  squeak,
  tag,
  mentions,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
