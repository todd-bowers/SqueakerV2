import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ITag } from 'app/shared/model/tag.model';
import { getEntities as getTags } from 'app/entities/tag/tag.reducer';
import { IMentions } from 'app/shared/model/mentions.model';
import { getEntities as getMentions } from 'app/entities/mentions/mentions.reducer';
import { ISqueak } from 'app/shared/model/squeak.model';
import { getEntity, updateEntity, createEntity, reset } from './squeak.reducer';

export const SqueakUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const tags = useAppSelector(state => state.tag.entities);
  const mentions = useAppSelector(state => state.mentions.entities);
  const squeakEntity = useAppSelector(state => state.squeak.entity);
  const loading = useAppSelector(state => state.squeak.loading);
  const updating = useAppSelector(state => state.squeak.updating);
  const updateSuccess = useAppSelector(state => state.squeak.updateSuccess);

  const handleClose = () => {
    navigate('/squeak');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getTags({}));
    dispatch(getMentions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.created = convertDateTimeToServer(values.created);

    const entity = {
      ...squeakEntity,
      ...values,
      tags: mapIdList(values.tags),
      mentions: mapIdList(values.mentions),
      user: users.find(it => it.id.toString() === values.user.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          created: displayDefaultDateTime(),
        }
      : {
          ...squeakEntity,
          created: convertDateTimeFromServer(squeakEntity.created),
          user: squeakEntity?.user?.id,
          tags: squeakEntity?.tags?.map(e => e.id.toString()),
          mentions: squeakEntity?.mentions?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="squeakerV2App.squeak.home.createOrEditLabel" data-cy="SqueakCreateUpdateHeading">
            Create or edit a Squeak
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="squeak-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Content"
                id="squeak-content"
                name="content"
                data-cy="content"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedBlobField label="Image" id="squeak-image" name="image" data-cy="image" isImage accept="image/*" />
              <ValidatedField
                label="Created"
                id="squeak-created"
                name="created"
                data-cy="created"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField label="Likes" id="squeak-likes" name="likes" data-cy="likes" type="text" />
              <ValidatedField id="squeak-user" name="user" data-cy="user" label="User" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Tag" id="squeak-tag" data-cy="tag" type="select" multiple name="tags">
                <option value="" key="0" />
                {tags
                  ? tags.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.hashtag}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Mentions" id="squeak-mentions" data-cy="mentions" type="select" multiple name="mentions">
                <option value="" key="0" />
                {mentions
                  ? mentions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.handle}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/squeak" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default SqueakUpdate;
