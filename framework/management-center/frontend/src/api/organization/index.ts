import { get, post, del, put } from "@commons/request";
import type { Ref } from "vue";
import type Result from "@commons/request/Result";
import type { Page } from "@commons/request/Result";
import type { CreateOrgFrom, OrgUpdateForm } from "@/api/organization/type";
import BaseOrganizationApi from "@commons/api/organization";
import type { Organization, ListOrganizationRequest } from "./type";
/**
 *  分页查询组织
 * @returns
 */
const pageOrganization: (
  req: ListOrganizationRequest,
  loading?: Ref<boolean>
) => Promise<Result<Page<Organization>>> = (req, loading) => {
  return get("/api/organization/page", req, loading);
};
/**
 * 查询到所有的组织
 * @returns 所有组织
 */
const listAllOrganization: (
  loading?: Ref<boolean>
) => Promise<Result<Array<Organization>>> = (loading) => {
  return get("/api/listAll/org", null, loading);
};

const batchSave = (data: CreateOrgFrom) => {
  return post("/api/organization/batch", null, data);
};
const deleteOrg = (id: string) => {
  return del("/api/organization/" + id);
};

const deleteBatchOrg = (organizations: Array<Organization>) => {
  return del("/api/organization", undefined, organizations);
};
const getOrgById = (id: string) => {
  return get("/api/organization/" + id, null);
};
const updateOrg = (organization: OrgUpdateForm) => {
  return put("/api/organization", undefined, organization);
};
export default {
  ...BaseOrganizationApi,
  pageOrganization,
  listAllOrganization,
  batchSave,
  deleteOrg,
  deleteBatchOrg,
  getOrgById,
  updateOrg,
};
