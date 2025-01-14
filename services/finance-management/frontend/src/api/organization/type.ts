import type {
  Organization,
  OrganizationTree,
} from "@commons/api/organization/type";
import type { Workspace } from "@commons/api/workspace/type";

interface OrganizationWorkspace {
  /**
   *主键id
   */
  id: string;
  /**
   *名称
   */
  name: string;
  /**
   * 组织或者工作空间
   */
  type: "WORKSPACE" | "ORGANIZATION";
}
/**
 * 组织或者工作空间树
 */
interface OrganizationWorkspaceTree extends OrganizationWorkspace {
  children?: Array<OrganizationWorkspaceTree>;
}
export type {
  Organization,
  OrganizationTree,
  Workspace,
  OrganizationWorkspaceTree,
  OrganizationWorkspace,
};
