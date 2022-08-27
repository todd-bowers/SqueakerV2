import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './squeak.reducer';

export const SqueakDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const squeakEntity = useAppSelector(state => state.squeak.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="squeakDetailsHeading">Squeak</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{squeakEntity.id}</dd>
          <dt>
            <span id="content">Content</span>
          </dt>
          <dd>{squeakEntity.content}</dd>
          <dt>
            <span id="image">Image</span>
          </dt>
          <dd>
            {squeakEntity.image ? (
              <div>
                {squeakEntity.imageContentType ? (
                  <a onClick={openFile(squeakEntity.imageContentType, squeakEntity.image)}>
                    <img src={`data:${squeakEntity.imageContentType};base64,${squeakEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                ) : null}
                <span>
                  {squeakEntity.imageContentType}, {byteSize(squeakEntity.image)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="created">Created</span>
          </dt>
          <dd>{squeakEntity.created ? <TextFormat value={squeakEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="likes">Likes</span>
          </dt>
          <dd>{squeakEntity.likes}</dd>
          <dt>User</dt>
          <dd>{squeakEntity.user ? squeakEntity.user.login : ''}</dd>
          <dt>Tag</dt>
          <dd>
            {squeakEntity.tags
              ? squeakEntity.tags.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.hashtag}</a>
                    {squeakEntity.tags && i === squeakEntity.tags.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
          <dt>Mentions</dt>
          <dd>
            {squeakEntity.mentions
              ? squeakEntity.mentions.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.handle}</a>
                    {squeakEntity.mentions && i === squeakEntity.mentions.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/squeak" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/squeak/${squeakEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SqueakDetail;
