import '@xyflow/react/dist/style.css';
import type { DependencyTree } from '../../../NPMFetch/DependencyTree';
import Tree from 'react-d3-tree';
import { useEffect, useRef, useState } from 'react';
import "../dependency-graph/DependencyGraph.css";
import NodeTreeVert from "../../../assets/DependencyTreeAssets/NodeTree_vert.svg";
import NodeTreeHorz from "../../../assets/DependencyTreeAssets/NodeTree_Horz.svg";
import {HoverToolTip} from "../../ToolTip/HoverToolTip.tsx";

interface DependencyGraphProps{
  dependencies: DependencyTree | null| undefined;
}
 
export default function DependencyGraph({dependencies}: DependencyGraphProps) {
  const tree_container_ref = useRef<HTMLDivElement>(null);
  const [position, setPosition] = useState({
    x: 0,
    y: 0
  });
  const data = {
    name: dependencies?.package_name,
    children: dependencies?.dependencies
  }

  useEffect(() => {
    if(tree_container_ref.current){
      const width = tree_container_ref.current.getBoundingClientRect().width;
      const height = tree_container_ref.current.getBoundingClientRect().height;

      setPosition({x: width/2, y: height/2});
    }
  }, [])
  return (
    <div style={{ width: '100%', height: '600px'}} ref={tree_container_ref} id="tree-container">
      <div id="tree-container-header-bar">

        <div id="tree-render-options">
          <div className="render-option-button">
            <HoverToolTip icon={NodeTreeVert} content="Vertical" scale="30px"/>
          </div>
          <div className="render-option-button">
            <HoverToolTip icon={NodeTreeHorz} content="Horizontal" scale="30px"/>
          </div>

        </div>
      </div>
      <div id="tree-container-body">
        {dependencies && <Tree data={data} orientation={'vertical'} translate={position} nodeSize={{x: 300, y: 300}}/>}
      </div>
    </div>
  );
}