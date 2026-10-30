import '@xyflow/react/dist/style.css';
import type { DependencyTree } from '../../../NPMFetchHook/DependencyTree.ts';
import { useEffect, useRef, useState } from 'react';
import "../dependency-graph/DependencyGraph.css";
import NodeTreeVert from "../../../assets/DependencyTreeAssets/NodeTree_vert.svg";
import NodeTreeHorz from "../../../assets/DependencyTreeAssets/NodeTree_Horz.svg";
import {HoverToolTip} from "../../ToolTip/HoverToolTip.tsx";
import { BiSad } from "react-icons/bi";
import Tree, { type Orientation, type RawNodeDatum } from 'react-d3-tree';
import type { Dependency } from '../../../NPMFetchHook/Dependency';

interface DependencyGraphProps{
  dependencies: DependencyTree | null| undefined;
}

function normalizeDependencies(dependencies: Dependency[]): RawNodeDatum[] {
    return dependencies.map(dependency => ({
        name: dependency.name,
        attributes: dependency.attributes
            ? {
                dependency_version: dependency.attributes.dependency_version ?? "Unknown",
                deprecated: dependency.attributes.deprecated
            }
            : undefined,
        children: dependency.children
            ? normalizeDependencies(dependency.children)
            : undefined
    }));
}
export default function DependencyGraph({dependencies}: DependencyGraphProps) {
  const tree_container_ref = useRef<HTMLDivElement>(null);
  const [renderOrientation, setRenderOrientation] = useState<Orientation>('vertical');
  const [position, setPosition] = useState({
    x: 0,
    y: 0
  });
  // const dependency_data = {
  //   name: dependencies?.package_name ?? "Unknown",
  //   children: dependencies?.dependencies ?? []
  // }
  const dependency_data: RawNodeDatum = {
    name: dependencies?.package_name ?? "Unknown",
    children: dependencies?.dependencies
        ? normalizeDependencies(dependencies.dependencies)
        : undefined
  };

  useEffect(() => {
    if(tree_container_ref.current){
      const width = tree_container_ref.current.getBoundingClientRect().width;
      const height = tree_container_ref.current.getBoundingClientRect().height;

      setPosition({ x: width/2, y: height/2});
    }
  }, [])
  return (
    <div style={{ width: '100%', height: '600px'}} ref={tree_container_ref} id="tree-container">
      <div id="tree-container-header-bar">

        <div id="tree-render-options">
          <div className="render-option-button">
            <HoverToolTip icon={NodeTreeVert} content="Horizontal" scale="30px" onPress={() => {setRenderOrientation("horizontal")}}/>
          </div>
          <div className="render-option-button">
            <HoverToolTip icon={NodeTreeHorz} content="Vertical" scale="30px" onPress={() => {setRenderOrientation("vertical")}}/>
          </div>

        </div>
      </div>
      <div id="tree-container-body">
        {dependencies ?
          <Tree data={dependency_data} orientation={renderOrientation} translate={position} nodeSize={{x: 300, y: 300}}/>
          :
          <div id="no-tree">
            <BiSad style={{"width": "50px", "height": "auto"}}/>
            <p>No Dependencies</p>
          </div>
        }
      </div>
    </div>
  );
}